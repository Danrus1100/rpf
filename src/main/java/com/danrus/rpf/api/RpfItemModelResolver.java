package com.danrus.rpf.api;

import com.danrus.rpf.duck.load.RpfModelManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface RpfItemModelResolver {
    void resolveAndAppendLayer(
            ItemStackRenderState renderState,
            ItemStack stack,
            ItemDisplayContext displayContext,
            Level level,
            LivingEntity entity,
            int seed,
            Operation<Void> vanilla,
            ItemModelResolver mcResolver
    );

    boolean shouldPlaySwapAnimation(ItemStack stack, Operation<Boolean> vanilla);

    public static RpfModelManager getModelManager() {
        return (RpfModelManager) Minecraft.getInstance().getModelManager();
    }
}
