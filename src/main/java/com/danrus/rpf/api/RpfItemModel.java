package com.danrus.rpf.api;

import com.danrus.rpf.api.codec.RpfModelsCodecsExtends;
import com.danrus.rpf.logging.ModelTestsResultCollector;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * An interface for item models that can act as a fallback and be used to determine if delegation should occur
 */
public interface RpfItemModel {
    void rpf$markAsFallback();
    boolean rpf$isFallback();

    default void rpf$doDelegate(
            ItemStackRenderState renderState,
            ItemStack stack,
            ItemModelResolver itemModelResolver,
            ItemDisplayContext displayContext,
            @Nullable ClientLevel level,
            @Nullable LivingEntity owner,
            @Nullable ItemModel prev,
            int seed,
            ResourceLocation itemModelId,
            String packName,
            ModelTestsResultCollector collector
    ) {
        if (rpf$isFallback()) {
            collector.delegate(this.getClass().getSimpleName(), packName);
        }
    }
}
