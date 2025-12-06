package com.danrus.rpf.mixin;

import com.danrus.rpf.duck.RpfItemModel;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(SelectItemModel.Unbaked.class)
public class SelectItemModelUnbakedMixin {
    @Inject(
            method = "bake",
            at = @At("RETURN")
    )
    private void rpf$setFallbackModel(ItemModel.BakingContext context, CallbackInfoReturnable<ItemModel> cir, @Local ItemModel itemModel) {
        ((RpfItemModel) itemModel).rpf$setFallback();
    }


}
