package com.danrus.rpf.mixin;

import com.danrus.rpf.duck.RpfItemModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RangeSelectItemModel.class)
public class RangeSelectItemModelMixin {
    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void rpf$init(RangeSelectItemModelProperty property, float scale, float[] thresholds, ItemModel[] models, ItemModel fallback, CallbackInfo ci){
        ((RpfItemModel) fallback).rpf$setFallback();
    }
}
