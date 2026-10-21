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
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.entity.animation.json.AnimationLoader;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Map;

/**
 * NeoForge 26.3 entry points for the ModelManager model-loading hooks.
 *
 * <p>NeoForge extends the vanilla signatures with standalone models and pending
 * animations. The actual logic lives in {@link ModelManagerHooks}.</p>
 */
@Mixin(ModelManager.class)
public abstract class ModelManagerPlatformMixin {

    @WrapOperation(
            method = "lambda$reload$1",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/model/ModelManager;discoverModelDependencies(Ljava/util/Map;Lnet/minecraft/client/resources/model/BlockStateModelLoader$LoadedModels;Lnet/minecraft/client/resources/model/ClientItemInfoLoader$LoadedClientInfos;Lnet/neoforged/neoforge/client/model/standalone/StandaloneModelLoader$LoadedModels;)Lnet/minecraft/client/resources/model/ModelManager$ResolvedModels;"
            )
    )
    private static ModelManager.ResolvedModels rpf$wrapDiscovery(
            Map<Identifier, UnbakedModel> inputModels,
            BlockStateModelLoader.LoadedModels loadedModels,
            ClientItemInfoLoader.LoadedClientInfos loadedClientInfos,
            StandaloneModelLoader.LoadedModels standaloneModels,
            Operation<ModelManager.ResolvedModels> original
    ) {
        return ModelManagerHooks.discoverModelDependencies(
                inputModels,
                loadedModels,
                ModelManagerHooks.currentItemLayers(),
                List.copyOf(standaloneModels.models().values()),
                infos -> original.call(inputModels, loadedModels, infos, standaloneModels)
        );
    }

    @WrapOperation(
            method = "lambda$reload$4",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/client/model/geom/EntityModelSet;Lnet/minecraft/client/resources/model/sprite/SpriteGetter;Lnet/minecraft/client/renderer/PlayerSkinRenderCache;Ljava/util/Map;Ljava/util/Map;Ljava/util/Map;Lnet/minecraft/client/resources/model/ResolvedModel;Lnet/neoforged/neoforge/client/model/standalone/StandaloneModelLoader$LoadedModels;Lnet/neoforged/neoforge/client/entity/animation/json/AnimationLoader$PendingAnimations;)Lnet/minecraft/client/resources/model/ModelBakery;"
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
            StandaloneModelLoader.LoadedModels standaloneModels,
            AnimationLoader.PendingAnimations pendingAnimations,
            Operation<ModelBakery> original
    ) {
        ModelBakery bakery = original.call(
                entityModelSet,
                sprites,
                playerSkinRenderCache,
                unbakedBlockStateModels,
                clientInfos,
                resolvedModels,
                missingModel,
                standaloneModels,
                pendingAnimations
        );
        ModelManagerHooks.onBakeryCreated(bakery);
        return bakery;
    }
}
