package com.danrus.rpf.mixin.items.range;

import com.danrus.rpf.api.RpfItemModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RangeSelectItemModel.class)
public abstract class RangeSelectItemModelMixin implements RpfItemModel {
    @Shadow
    @Final
    private ItemModel fallback;

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void rpf$init(RangeSelectItemModelProperty property, float scale, float[] thresholds, ItemModel[] models, ItemModel fallback, CallbackInfo ci){
        ((RpfItemModel) fallback).rpf$markAsFallback();
    }

    @Override
    public boolean rpf$testForDelegate(
            ItemStackRenderState renderState,
            ItemStack stack,
            ItemModelResolver itemModelResolver,
            ItemDisplayContext displayContext,
            @Nullable ClientLevel level,
            @Nullable LivingEntity owner,
            int seed,
            ResourceLocation itemModelId
    ) {
        if (this.rpf$isFallback()) return true;
        return (((RpfItemModel) fallback).rpf$testForDelegate(renderState, stack, itemModelResolver, displayContext, level, owner, seed, itemModelId));
    }
}
