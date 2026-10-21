package com.danrus.rpf.duck.load;

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
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Implemented by the platform-specific ModelBakery mixin so the common baking code
 * can create platform-specific {@link ItemModel.BakingContext} and
 * {@link ModelBakery.BakingResult} instances without referencing loader-only types.
 */
public interface RpfBakingContextFactory {
    ItemModel.BakingContext rpf$createBakingContext(
            ModelBaker baker,
            EntityModelSet entityModelSet,
            SpriteGetter sprites,
            PlayerSkinRenderCache playerSkinRenderCache,
            MissingItemModel missingItemModel,
            @Nullable RegistryContextSwapper contextSwapper
    );

    ModelBakery.BakingResult rpf$createBakingResult(
            ModelBakery.BakingResult vanilla,
            Map<Identifier, ItemModel> itemModels,
            Map<Identifier, ClientItem.Properties> itemProperties
    );
}
