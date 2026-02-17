package com.danrus.rpf.mixin.load;

import com.danrus.rpf.Rpf;
import com.danrus.rpf.api.event.type.PostBakeEvent;
import com.danrus.rpf.api.event.type.PreBakeEvent;
import com.danrus.rpf.core.RpfModelIdentity;
import com.danrus.rpf.core.SignedItemModel;
import com.danrus.rpf.duck.RpfClientItem;
import com.danrus.rpf.duck.load.RpfBakingResult;
import com.danrus.rpf.duck.load.RpfModelBakery;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.Util;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.SpriteGetter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.thread.ParallelMapTransform;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.*;
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
public class ModelBakeryMixin implements RpfModelBakery {

    @Unique
    private static final Logger LOGGER = LoggerFactory.getLogger("RpfModelBakery");

    @Unique
    private List<Map<ResourceLocation, ClientItem>> rpf$clientItems;

    @Shadow
    @Final
    private EntityModelSet entityModelSet;

    //? if >=1.21.10{

    /*@Shadow
    @Final
    private net.minecraft.client.renderer.PlayerSkinRenderCache playerSkinRenderCache;

    @Shadow
    @Final
    private net.minecraft.client.resources.model.MaterialSet materials;

    *///? }




    @Override
    public ModelBakery rpf$setClientItems(List<Map<ResourceLocation, ClientItem>> items) {
        this.rpf$clientItems = items;
        return (ModelBakery) (Object) this;
    }

    @Inject(
            method = "bakeModels",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/thread/ParallelMapTransform;schedule(Ljava/util/Map;Ljava/util/function/BiFunction;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;", ordinal = 1),
            cancellable = true
    )
    private void rpf$bakeModels(SpriteGetter sprites, Executor executor, CallbackInfoReturnable<CompletableFuture<ModelBakery.BakingResult>> cir,
                                @Local ModelBakery.MissingModels missingModels,
                                @Local ModelBakery.ModelBakerImpl modelBakerImpl,
                                @Local CompletableFuture<Map<BlockState, BlockStateModel>> completableFuture) {
        List<CompletableFuture<Map<ResourceLocation, SignedItemModel>>> layerFutures = new ArrayList<>(this.rpf$clientItems.size());

        for (Map<ResourceLocation, ClientItem> layer : this.rpf$clientItems) {
            CompletableFuture<Map<ResourceLocation, SignedItemModel>> layerFuture = ParallelMapTransform.schedule(
                    layer,
                    (resourceLocation, clientItem) -> {
                        try {
                            ItemModel.BakingContext context = new ItemModel.BakingContext(
                                    modelBakerImpl,
                                    this.entityModelSet,
                                    //? if >=1.21.10{
                                    /*materials,
                                    playerSkinRenderCache,
                                    *///?}
                                    missingModels.item,
                                    clientItem.registrySwapper());
                            PreBakeEvent preEvent = new PreBakeEvent(clientItem, resourceLocation, context);
                            Rpf.getEventBus().post(preEvent);
                            if (preEvent.isCancelled()) return null;
                            ItemModel model = clientItem.model().bake(preEvent.getBakingContext());
                            SignedItemModel result = new SignedItemModel(RpfClientItem.class.cast(clientItem).rpf$getPackName(), model);
                            PostBakeEvent postEvent = new PostBakeEvent(clientItem, result);
                            Rpf.getEventBus().post(postEvent);
                            if (postEvent.isCancelled()) return null;
                            return postEvent.getResult();
                        } catch (Exception exception) {
                            LOGGER.warn("Unable to bake item model: '{}'", resourceLocation, exception);
                            return null;
                        }
                    },
                    executor
            );
            layerFutures.add(layerFuture);
        }
        List<Map<ResourceLocation, ClientItem.Properties>> propertiesLayers = new ArrayList<>(this.rpf$clientItems.size());
        Map<RpfModelIdentity, ClientItem.Properties> byIdentity = new HashMap<>();
        for (Map<ResourceLocation, ClientItem> layer : this.rpf$clientItems) {
            Map<ResourceLocation, ClientItem.Properties> propertiesMap = new HashMap<>();
            layer.forEach((resourceLocation, clientItem) -> {
                ClientItem.Properties properties = clientItem.properties();
                if (!properties.equals(ClientItem.Properties.DEFAULT)) {
                    propertiesMap.put(resourceLocation, properties);
                    RpfModelIdentity identity = new RpfModelIdentity(resourceLocation, RpfClientItem.class.cast(clientItem).rpf$getPackName());
                    byIdentity.put(identity, properties);
                }
            });
            propertiesLayers.add(propertiesMap);
        }

        cir.setReturnValue(completableFuture.thenCombine(Util.sequence(layerFutures), (blockModels, bakedLayers) -> {

            Map<ResourceLocation, ItemModel> flatItemModels = new HashMap<>();
            for (Map<ResourceLocation, SignedItemModel> layer : bakedLayers) {
                for (Map.Entry<ResourceLocation, SignedItemModel> m : layer.entrySet()) {
                    flatItemModels.put(m.getKey(), m.getValue().model());
                }
            }

            ModelBakery.BakingResult result = new ModelBakery.BakingResult(missingModels, blockModels, flatItemModels, Map.of());
            ((RpfBakingResult) (Object) result)
                    .rpf$addItemPropertiesByIdentity(byIdentity)
                    .rpf$setItemPropertiesById(propertiesLayers)
                    .rpf$setSignedItemModels(bakedLayers);

            return result;
        }));

    }
}
