package com.danrus.rpf.api;

import com.danrus.rpf.core.item.ModelUpdateContext;
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

    default boolean rpf$doDelegate(
            ModelUpdateContext context,
            ItemStack stack,
            @Nullable LivingEntity owner,
            @Nullable ItemModel prev,
            TestsResultCollector collector
    ) {
        if (rpf$isFallback()) {
            collector.delegate(this.getClass(), "");
            return true;
        }
        return false;
    }
}
