package com.danrus.rpf.mixin.load;

import com.danrus.rpf.api.RpfItemModel;
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
        List<CompletableFuture<Map<ResourceLocation, ItemModel>>> layerFutures = new ArrayList<>(this.rpf$clientItems.size());

        for (int i = 0; i < this.rpf$clientItems.size(); i++) {
            int layerIndex = i;
            Map<ResourceLocation, ClientItem> layer = this.rpf$clientItems.get(i);
            CompletableFuture<Map<ResourceLocation, ItemModel>> layerFuture = ParallelMapTransform.schedule(
                    layer,
                    (resourceLocation, clientItem) -> {
                        try {
                            ItemModel model = clientItem.model().bake(new ItemModel.BakingContext(modelBakerImpl, this.entityModelSet, missingModels.item, clientItem.registrySwapper()));
                            return model;
                        } catch (Exception exception) {
                            LOGGER.warn("Unable to bake item model: '{}'", resourceLocation, exception);
                            return null;
                        }
                    },
                    executor
            );
            layerFutures.add(layerFuture);
        }

        Map<ResourceLocation, ClientItem.Properties> propertiesMap = new HashMap<>();
        for (Map<ResourceLocation, ClientItem> layer : this.rpf$clientItems) {
            layer.forEach((resourceLocation, clientItem) -> {
                ClientItem.Properties properties = clientItem.properties();
                if (!properties.equals(ClientItem.Properties.DEFAULT)) {
                    propertiesMap.put(resourceLocation, properties);
                }
            });
        }

        cir.setReturnValue(completableFuture.thenCombine(Util.sequence(layerFutures), (blockModels, bakedLayers) -> {

            Map<ResourceLocation, ItemModel> flatItemModels = new HashMap<>();
            for (Map<ResourceLocation, ItemModel> layer : bakedLayers) {
                flatItemModels.putAll(layer);
            }

            ModelBakery.BakingResult result = new ModelBakery.BakingResult(missingModels, blockModels, flatItemModels, propertiesMap);
            ((RpfBakingResult) (Object) result).rpf$setItemModels(bakedLayers);

            return result;
        }));

    }
}
