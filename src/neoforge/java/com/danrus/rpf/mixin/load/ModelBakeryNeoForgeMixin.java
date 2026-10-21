package com.danrus.rpf.mixin.load;

import com.danrus.rpf.duck.load.RpfBakingContextFactory;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.MissingItemModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RegistryContextSwapper;
import net.neoforged.neoforge.client.entity.animation.json.AnimationLoader;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelLoader;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * NeoForge 26.3 adds {@code pendingAnimations} to {@link ItemModel.BakingContext}
 * and {@code standaloneModels} to {@link ModelBakery.BakingResult}.
 *
 * <p>The Modstitch compile classpath exposes the unpatched Minecraft signatures,
 * while NeoForge applies these extensions at runtime. The constructors/methods are
 * therefore resolved reflectively; the non-extended fallback keeps the code
 * working if NeoForge changes its patches again.</p>
 */
@Mixin(ModelBakery.class)
public abstract class ModelBakeryNeoForgeMixin implements RpfBakingContextFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger("RpfModelBakery");

    @Nullable
    private static final Constructor<ItemModel.BakingContext> PENDING_CONTEXT_CONSTRUCTOR =
            findPendingContextConstructor();

    @Nullable
    private static final Constructor<ModelBakery.BakingResult> RESULT_WITH_STANDALONE_CONSTRUCTOR =
            findResultWithStandaloneConstructor();

    @Nullable
    private static final Method STANDALONE_MODELS_METHOD = findStandaloneModelsMethod();

    @Shadow
    @Final
    private AnimationLoader.PendingAnimations pendingAnimations;

    @Override
    public ItemModel.BakingContext rpf$createBakingContext(
            ModelBaker baker,
            EntityModelSet entityModelSet,
            SpriteGetter sprites,
            PlayerSkinRenderCache playerSkinRenderCache,
            MissingItemModel missingItemModel,
            @Nullable RegistryContextSwapper contextSwapper
    ) {
        if (PENDING_CONTEXT_CONSTRUCTOR != null) {
            try {
                return PENDING_CONTEXT_CONSTRUCTOR.newInstance(
                        baker,
                        entityModelSet,
                        sprites,
                        playerSkinRenderCache,
                        missingItemModel,
                        contextSwapper,
                        this.pendingAnimations
                );
            } catch (ReflectiveOperationException exception) {
                LOGGER.warn("[RPF] Failed to create an animated baking context; animations will be empty.", exception);
            }
        }

        return new ItemModel.BakingContext(
                baker,
                entityModelSet,
                sprites,
                playerSkinRenderCache,
                missingItemModel,
                contextSwapper
        );
    }

    @Override
    public ModelBakery.BakingResult rpf$createBakingResult(
            ModelBakery.BakingResult vanilla,
            Map<Identifier, ItemModel> itemModels,
            Map<Identifier, ClientItem.Properties> itemProperties
    ) {
        if (RESULT_WITH_STANDALONE_CONSTRUCTOR != null && STANDALONE_MODELS_METHOD != null) {
            try {
                Object standaloneModels = STANDALONE_MODELS_METHOD.invoke(vanilla);
                return RESULT_WITH_STANDALONE_CONSTRUCTOR.newInstance(
                        vanilla.missingModels(),
                        vanilla.blockStateModels(),
                        itemModels,
                        itemProperties,
                        standaloneModels
                );
            } catch (ReflectiveOperationException exception) {
                LOGGER.warn("[RPF] Failed to preserve NeoForge standalone models in the baking result.", exception);
            }
        }

        return new ModelBakery.BakingResult(
                vanilla.missingModels(),
                vanilla.blockStateModels(),
                itemModels,
                itemProperties
        );
    }

    @Nullable
    private static Constructor<ItemModel.BakingContext> findPendingContextConstructor() {
        try {
            return ItemModel.BakingContext.class.getConstructor(
                    ModelBaker.class,
                    EntityModelSet.class,
                    SpriteGetter.class,
                    PlayerSkinRenderCache.class,
                    MissingItemModel.class,
                    RegistryContextSwapper.class,
                    AnimationLoader.PendingAnimations.class
            );
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }

    @Nullable
    private static Constructor<ModelBakery.BakingResult> findResultWithStandaloneConstructor() {
        try {
            return ModelBakery.BakingResult.class.getConstructor(
                    ModelBakery.MissingModels.class,
                    Map.class,
                    Map.class,
                    Map.class,
                    StandaloneModelLoader.BakedModels.class
            );
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }

    @Nullable
    private static Method findStandaloneModelsMethod() {
        try {
            return ModelBakery.BakingResult.class.getMethod("standaloneModels");
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }
}
