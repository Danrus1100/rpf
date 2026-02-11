package com.danrus.rpf.api;

import com.danrus.rpf.logging.ModelTestsResultCollector;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * An interface for item models that can act as a fallback and be used to determine if delegation should occur
 */
public interface RpfItemModel {
    void rpf$markAsFallback();
    boolean rpf$isFallback();

    default boolean rpf$testForDelegate(
            ItemStackRenderState renderState,
            ItemStack stack,
            ItemModelResolver itemModelResolver,
            ItemDisplayContext displayContext,
            @Nullable ClientLevel level,
            @Nullable ItemOwner owner,
            int seed,
            Identifier itemModelId,
            String packName,
            ModelTestsResultCollector collector
    ) {
        if (!rpf$isFallback()) {
            collector.touchDelegate(this.getClass().getSimpleName(), packName, itemModelId);
            return false;
        }
        return true;
    }
}
