package com.danrus.rpf.mixin.load;

import com.danrus.rpf.core.load.ModelManagerHooks;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.client.resources.model.ClientItemInfoLoader;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Map;

/**
 * Fabric entry points for the ModelManager model-loading hooks.
 */
@Mixin(ModelManager.class)
public abstract class ModelManagerPlatformMixin {

    @WrapOperation(
            method = "lambda$reload$1",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/model/ModelManager;discoverModelDependencies(Ljava/util/Map;Lnet/minecraft/client/resources/model/BlockStateModelLoader$LoadedModels;Lnet/minecraft/client/resources/model/ClientItemInfoLoader$LoadedClientInfos;)Lnet/minecraft/client/resources/model/ModelManager$ResolvedModels;"
            )
    )
    private static ModelManager.ResolvedModels rpf$wrapDiscovery(
            Map<Identifier, UnbakedModel> inputModels,
            BlockStateModelLoader.LoadedModels loadedModels,
            ClientItemInfoLoader.LoadedClientInfos loadedClientInfos,
            Operation<ModelManager.ResolvedModels> original
    ) {
        return ModelManagerHooks.discoverModelDependencies(
                inputModels,
                loadedModels,
                ModelManagerHooks.currentItemLayers(),
                List.<ResolvableModel>of(),
                infos -> original.call(inputModels, loadedModels, infos)
        );
    }

    @WrapOperation(
            method = "lambda$reload$4",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/client/model/geom/EntityModelSet;Lnet/minecraft/client/resources/model/sprite/SpriteGetter;Lnet/minecraft/client/renderer/PlayerSkinRenderCache;Ljava/util/Map;Ljava/util/Map;Ljava/util/Map;Lnet/minecraft/client/resources/model/ResolvedModel;)Lnet/minecraft/client/resources/model/ModelBakery;"
            )
    )
    private static ModelBakery rpf$redirectModelBakeryConstructor(
            EntityModelSet entityModelSet,
            SpriteGetter sprites,
            PlayerSkinRenderCache playerSkinRenderCache,
            Map<BlockState, BlockStateModel.UnbakedRoot> unbakedBlockStateModels,
            Map<Identifier, ClientItem> clientInfos,
            Map<Identifier, ResolvedModel> resolvedModels,
            ResolvedModel missingModel,
            Operation<ModelBakery> original
    ) {
        ModelBakery bakery = original.call(
                entityModelSet,
                sprites,
                playerSkinRenderCache,
                unbakedBlockStateModels,
                clientInfos,
                resolvedModels,
                missingModel
        );
        ModelManagerHooks.onBakeryCreated(bakery);
        return bakery;
    }
}
