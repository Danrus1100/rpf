package com.danrus.rpf.impl;

import com.danrus.rpf.api.RpfItemModelResolver;
import com.danrus.rpf.core.item.ModelUpdateContext;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class VanillaModelResolver implements RpfItemModelResolver {

    @Override
    public void resolveAndAppendLayer(ModelUpdateContext context, ItemStack stack, LivingEntity entity, Operation<Void> vanilla) {
        vanilla.call(context.renderState(), stack, context.displayContext(), context.level(), entity, context.seed());
    }

    @Override
    public boolean shouldPlaySwapAnimation(ItemStack stack, Operation<Boolean> vanilla) {
        return vanilla.call(stack);
    }
}
