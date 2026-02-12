package com.danrus.rpf.core;

import com.danrus.rpf.api.RpfItemModel;
import com.danrus.rpf.logging.ModelTestsResultCollector;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record SignedItemModel(
        String name,
        ItemModel model
) {
    public boolean doDelegate(
            ItemStackRenderState renderState,
            ItemStack stack,
            ItemModelResolver itemModelResolver,
            ItemDisplayContext displayContext,
            @Nullable ClientLevel level,
            @Nullable LivingEntity owner,
            int seed,
            ResourceLocation itemModelId,
            ModelTestsResultCollector collector
    ) {
        if (model == null) return false;
        try {
            return ((RpfItemModel)model).rpf$doDelegate(
                renderState,
                stack,
                itemModelResolver,
                displayContext,
                level,
                owner,
                null,
                seed,
                itemModelId,
                name,
                collector
            );
        } catch (Exception ignored) {
            return false;
        }
    }

    public void update(ItemStackRenderState renderState, ItemStack stack, ItemModelResolver itemModelResolver, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed) {
        if (model != null) {
            model.update(renderState, stack, itemModelResolver, displayContext, level, owner, seed);
        }
    }

}
