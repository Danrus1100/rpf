package com.danrus.rpf.mixin.items.select;

import com.danrus.rpf.RpfCodecs;
import com.danrus.rpf.api.RpfItemModel;
import com.danrus.rpf.api.DelegateItemModel;
import com.danrus.rpf.duck.item.RpfSelectModelUnbakedSwitch;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SelectItemModel.Unbaked.class)
public class UnbakedMixin implements DelegateItemModel.Unbaked {
    @Inject(
            method = "type",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void rpf$type(CallbackInfoReturnable<MapCodec<SelectItemModel.Unbaked>> cir) {
        cir.setReturnValue(RpfCodecs.MAP_CODEC_SELECT);
    }


    @Unique
    private boolean rpf$doDelegate = true;

    @Shadow
    @Final
    private SelectItemModel.UnbakedSwitch<?, ?> unbakedSwitch;


    @Inject(
            method = "bake",
            at = @At("HEAD")
    )
    private void rpf$bakeDelegationForSwitch(ItemModel.BakingContext context, CallbackInfoReturnable<ItemModel> cir) {
        ((RpfSelectModelUnbakedSwitch) (Object) unbakedSwitch).rpf$setUnbakedDelegation(rpf$doDelegate);
    }

    @Inject(
            method = "bake",
            at = @At("RETURN")
    )
    private void rpf$setFallbackModel(ItemModel.BakingContext context, CallbackInfoReturnable<ItemModel> cir, @Local ItemModel itemModel) {
        ((RpfItemModel) itemModel).rpf$setFallback();
    }


    @Override
    public boolean rpf$delegate() {
        return rpf$doDelegate;
    }

    @Override
    public void rpf$setDeligation(boolean value) {
        this.rpf$doDelegate = value;
    }
}
