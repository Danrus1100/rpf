package com.danrus.rpf.impl;

import com.danrus.rpf.api.RpfItemModelResolver;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class VanillaModelResolver implements RpfItemModelResolver {
    @Override
    public void resolveAndAppendLayer(ItemStackRenderState renderState, ItemStack stack, ItemDisplayContext displayContext, Level level, LivingEntity entity, int seed, Operation<Void> vanilla, ItemModelResolver mcResolver) {
        vanilla.call(renderState, stack, displayContext, level, entity, seed);
    }

    @Override
    public boolean shouldPlaySwapAnimation(ItemStack stack, Operation<Boolean> vanilla) {
        return vanilla.call(stack);
    }
}
