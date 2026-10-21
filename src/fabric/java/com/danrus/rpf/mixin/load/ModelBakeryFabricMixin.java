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
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Map;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryFabricMixin implements RpfBakingContextFactory {

    @Override
    public ItemModel.BakingContext rpf$createBakingContext(
            ModelBaker baker,
            EntityModelSet entityModelSet,
            SpriteGetter sprites,
            PlayerSkinRenderCache playerSkinRenderCache,
            MissingItemModel missingItemModel,
            @Nullable RegistryContextSwapper contextSwapper
    ) {
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
        return new ModelBakery.BakingResult(
                vanilla.missingModels(),
                vanilla.blockStateModels(),
                itemModels,
                itemProperties
        );
    }
}
