package com.danrus.rpf.mixin.load;

import com.danrus.rpf.Rpf;
import com.danrus.rpf.api.event.type.PostBakeEvent;
import com.danrus.rpf.api.event.type.PreBakeEvent;
import com.danrus.rpf.core.item.RpfModelIdentity;
import com.danrus.rpf.core.item.SignedItemModel;
import com.danrus.rpf.duck.RpfClientItem;
import com.danrus.rpf.duck.load.RpfBakingContextFactory;
import com.danrus.rpf.duck.load.RpfBakingResult;
import com.danrus.rpf.duck.load.RpfModelBakery;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.thread.ParallelMapTransform;
import org.joml.Matrix4fc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin implements RpfModelBakery {

    @Unique
    private static final Logger LOGGER = LoggerFactory.getLogger("RpfModelBakery");

    @Unique
    private List<Map<Identifier, ClientItem>> rpf$clientItems;

    @Unique
    private List<CompletableFuture<Map<Identifier, SignedItemModel>>> rpf$layerFutures;

    @Unique
    private List<Map<Identifier, ClientItem.Properties>> rpf$propertiesLayers;

    @Unique
    private Map<RpfModelIdentity, ClientItem.Properties> rpf$propertiesByIdentity;

    @Shadow
    @Final
    private EntityModelSet entityModelSet;

    @Shadow
    @Final
    private PlayerSkinRenderCache playerSkinRenderCache;

    @Shadow
    @Final
    private SpriteGetter sprites;

    @Shadow
    @Final
    private static Matrix4fc IDENTITY;

    @Override
    public ModelBakery rpf$setClientItems(List<Map<Identifier, ClientItem>> items) {
        this.rpf$clientItems = items;
        return (ModelBakery) (Object) this;
    }

    /**
     * Starts one baking task per resource pack layer. Cancelling the whole vanilla method
     * would also skip NeoForge's standalone-model baking, so the result is merged at RETURN.
     */
    @Inject(
            method = "bakeModels",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/thread/ParallelMapTransform;schedule(Ljava/util/Map;Ljava/util/function/BiFunction;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;",
                    ordinal = 1
            )
    )
    private void rpf$collectBakeTasks(
            MaterialBaker materials,
            Executor executor,
            @Local ModelBakery.MissingModels missingModels,
            @Local ModelBakery.ModelBakerImpl modelBakerImpl
    ) {
        if (this.rpf$clientItems == null) {
            LOGGER.warn("[RPF] ModelBakery was created without RPF item layers; item models will be empty.");
            return;
        }

        List<CompletableFuture<Map<Identifier, SignedItemModel>>> layerFutures =
                new ArrayList<>(this.rpf$clientItems.size());
        for (Map<Identifier, ClientItem> layer : this.rpf$clientItems) {
            layerFutures.add(ParallelMapTransform.schedule(
                    layer,
                    (resourceLocation, clientItem) -> rpf$bakeModel(modelBakerImpl, missingModels, resourceLocation, clientItem),
                    executor
            ));
        }

        List<Map<Identifier, ClientItem.Properties>> propertiesLayers =
                new ArrayList<>(this.rpf$clientItems.size());
        Map<RpfModelIdentity, ClientItem.Properties> byIdentity = new HashMap<>();
        for (Map<Identifier, ClientItem> layer : this.rpf$clientItems) {
            Map<Identifier, ClientItem.Properties> propertiesMap = new HashMap<>();
            layer.forEach((resourceLocation, clientItem) -> {
                ClientItem.Properties properties = clientItem.properties();
                propertiesMap.put(resourceLocation, properties);
                if (!properties.equals(ClientItem.Properties.DEFAULT)) {
                    byIdentity.put(
                            new RpfModelIdentity(
                                    resourceLocation,
                                    RpfClientItem.class.cast(clientItem).rpf$getPackName()
                            ),
                            properties
                    );
                }
            });
            propertiesLayers.add(propertiesMap);
        }

        this.rpf$layerFutures = layerFutures;
        this.rpf$propertiesLayers = propertiesLayers;
        this.rpf$propertiesByIdentity = byIdentity;
    }

    private SignedItemModel rpf$bakeModel(
            ModelBakery.ModelBakerImpl modelBakerImpl,
            ModelBakery.MissingModels missingModels,
            Identifier resourceLocation,
            ClientItem clientItem
    ) {
        try {
            ItemModel.BakingContext context = rpf$createBakingContext(modelBakerImpl, missingModels, clientItem);
            PreBakeEvent preEvent = new PreBakeEvent(clientItem, resourceLocation, context);
            Rpf.getEventBus().post(preEvent);
            if (preEvent.isCancelled()) return null;

            ItemModel model = clientItem.model().bake(preEvent.getBakingContext(), IDENTITY);
            SignedItemModel result = new SignedItemModel(
                    RpfClientItem.class.cast(clientItem).rpf$getPackLocationInfo(),
                    model
            );

            PostBakeEvent postEvent = new PostBakeEvent(clientItem, result);
            Rpf.getEventBus().post(postEvent);
            if (postEvent.isCancelled()) return null;
            return postEvent.getResult();
        } catch (Exception exception) {
            LOGGER.warn("Unable to bake item model: '{}'", resourceLocation, exception);
            return null;
        }
    }

    private ItemModel.BakingContext rpf$createBakingContext(
            ModelBakery.ModelBakerImpl modelBakerImpl,
            ModelBakery.MissingModels missingModels,
            ClientItem clientItem
    ) {
        if (this instanceof RpfBakingContextFactory factory) {
            return factory.rpf$createBakingContext(
                    modelBakerImpl,
                    this.entityModelSet,
                    this.sprites,
                    this.playerSkinRenderCache,
                    missingModels.item(),
                    clientItem.registrySwapper()
            );
        }
        return new ItemModel.BakingContext(
                modelBakerImpl,
                this.entityModelSet,
                this.sprites,
                this.playerSkinRenderCache,
                missingModels.item(),
                clientItem.registrySwapper()
        );
    }

    @Inject(method = "bakeModels", at = @At("RETURN"), cancellable = true)
    private void rpf$combineBakeResult(
            MaterialBaker materials,
            Executor executor,
            CallbackInfoReturnable<CompletableFuture<ModelBakery.BakingResult>> cir
    ) {
        if (this.rpf$layerFutures == null
                || this.rpf$propertiesLayers == null
                || this.rpf$propertiesByIdentity == null) {
            return;
        }

        CompletableFuture<ModelBakery.BakingResult> vanillaFuture = cir.getReturnValue();
        cir.setReturnValue(vanillaFuture.thenCombine(Util.sequence(this.rpf$layerFutures), (vanillaResult, bakedLayers) -> {
            Map<Identifier, ItemModel> flatItemModels = new HashMap<>();
            Map<Identifier, ClientItem.Properties> flatItemProperties = new HashMap<>();

            for (int layerIndex = 0; layerIndex < bakedLayers.size(); layerIndex++) {
                Map<Identifier, SignedItemModel> layer = bakedLayers.get(layerIndex);
                Map<Identifier, ClientItem.Properties> properties = this.rpf$propertiesLayers.get(layerIndex);
                for (Map.Entry<Identifier, SignedItemModel> entry : layer.entrySet()) {
                    flatItemModels.put(entry.getKey(), entry.getValue().model());
                    flatItemProperties.put(
                            entry.getKey(),
                            properties.getOrDefault(entry.getKey(), ClientItem.Properties.DEFAULT)
                    );
                }
            }

            ModelBakery.BakingResult result = rpf$createBakingResult(vanillaResult, flatItemModels, flatItemProperties);
            RpfBakingResult rpfResult = (RpfBakingResult) (Object) result;
            rpfResult.rpf$setSignedItemModels(bakedLayers);
            rpfResult.rpf$setItemPropertiesById(this.rpf$propertiesLayers);
            rpfResult.rpf$addItemPropertiesByIdentity(this.rpf$propertiesByIdentity);
            return result;
        }));
    }

    private ModelBakery.BakingResult rpf$createBakingResult(
            ModelBakery.BakingResult vanilla,
            Map<Identifier, ItemModel> itemModels,
            Map<Identifier, ClientItem.Properties> itemProperties
    ) {
        if (this instanceof RpfBakingContextFactory factory) {
            return factory.rpf$createBakingResult(vanilla, itemModels, itemProperties);
        }
        return new ModelBakery.BakingResult(
                vanilla.missingModels(),
                vanilla.blockStateModels(),
                itemModels,
                itemProperties
        );
    }
}
