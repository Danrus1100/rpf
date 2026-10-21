package com.danrus.rpf.mixin.load;

import com.danrus.rpf.core.item.RpfModelIdentity;
import com.danrus.rpf.core.item.RpfResolversManager;
import com.danrus.rpf.core.item.SignedItemModel;
import com.danrus.rpf.core.load.ModelManagerHooks;
import com.danrus.rpf.core.load.ResourceLoadManager;
import com.danrus.rpf.debug.RpfDebugSystem;
import com.danrus.rpf.duck.load.RpfBakingResult;
import com.danrus.rpf.duck.load.RpfModelManager;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.ClientItemInfoLoader;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ModelManager.class)
public abstract class ModelManagerMixin implements RpfModelManager {

    @Unique
    private static final Logger LOGGER = LoggerFactory.getLogger("RpfModelManager");

    @Unique
    private List<Map<Identifier, SignedItemModel>> rpf$bakedItemStackSignetModels = List.of();

    @Unique
    private List<Map<Identifier, ClientItem.Properties>> rpf$itemProperties = List.of();

    @Unique
    private Map<RpfModelIdentity, ClientItem.Properties> rpf$itemPropertiesByIdentity = Map.of();

    @Shadow
    private ModelBakery.MissingModels missingModels;

    @WrapOperation(
            method = "reload",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/model/ClientItemInfoLoader;scheduleLoad(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"
            )
    )
    private CompletableFuture<ClientItemInfoLoader.LoadedClientInfos> rpf$wrapClientItemInfoLoader(
            ResourceManager resourceManager,
            Executor executor,
            Operation<CompletableFuture<ClientItemInfoLoader.LoadedClientInfos>> original
    ) {
        return ModelManagerHooks.scheduleClientItems(resourceManager, executor);
    }

    @Inject(
            method = "apply",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/model/ModelBakery$BakingResult;itemStackModels()Ljava/util/Map;"
            )
    )
    private void rpf$apply(
            ModelManager.ReloadState reloadState,
            CallbackInfo ci,
            @Local ModelBakery.BakingResult bakingResult
    ) {
        try {
            RpfBakingResult result = (RpfBakingResult) (Object) bakingResult;

            List<Map<Identifier, SignedItemModel>> signedModels = result.rpf$getItemSignedModels();
            if (signedModels != null) {
                // Reversed so the highest-priority pack is first and vanilla is last.
                this.rpf$bakedItemStackSignetModels = new ArrayList<>(signedModels.reversed());
            }

            List<Map<Identifier, ClientItem.Properties>> properties = result.rpf$getItemPropertiesById();
            if (properties != null) {
                this.rpf$itemProperties = new ArrayList<>(properties.reversed());
            }

            Map<RpfModelIdentity, ClientItem.Properties> propertiesByIdentity =
                    result.rpf$getItemPropertiesByIdentity();
            if (propertiesByIdentity != null) {
                this.rpf$itemPropertiesByIdentity = propertiesByIdentity;
            }

            ResourceLoadManager.vanillaModelCache.clear();
            RpfResolversManager.getInstance().applyPendingResolver();
            RpfDebugSystem.getInstance().clear();
        } catch (Exception exception) {
            LOGGER.error("[RPF] Failed to apply baked RPF model data; keeping vanilla model data.", exception);
        }
    }

    @WrapMethod(method = "getItemModel")
    private ItemModel rpf$wrapGetItemModel(Identifier modelLocation, Operation<ItemModel> original) {
        for (Map<Identifier, SignedItemModel> layer : this.rpf$bakedItemStackSignetModels) {
            SignedItemModel model = layer.get(modelLocation);
            if (model != null) {
                return model.model();
            }
        }
        return original.call(modelLocation);
    }

    @Override
    public List<Map<Identifier, SignedItemModel>> rpf$getSignedModels() {
        return this.rpf$bakedItemStackSignetModels;
    }

    @Override
    public List<Map<Identifier, ClientItem.Properties>> rpf$getItemPropertiesMaps() {
        return this.rpf$itemProperties;
    }

    @Override
    public SignedItemModel rpf$getVanillaModel(Identifier location) {
        SignedItemModel cached = ResourceLoadManager.vanillaModelCache.get(location);
        if (cached != null) {
            return cached;
        }

        for (Map<Identifier, SignedItemModel> map : this.rpf$bakedItemStackSignetModels) {
            SignedItemModel model = map.get(location);
            if (model != null && "vanilla".equals(model.name())) {
                ResourceLoadManager.vanillaModelCache.put(location, model);
                return model;
            }
        }
        return null;
    }

    @Override
    @NotNull
    public ClientItem.Properties rpf$getProperties(RpfModelIdentity identity) {
        ClientItem.Properties properties = this.rpf$itemPropertiesByIdentity.get(identity);
        return properties == null ? ClientItem.Properties.DEFAULT : properties;
    }

    @Override
    public ItemModel rpf$getMissingModel() {
        return this.missingModels.item();
    }
}
