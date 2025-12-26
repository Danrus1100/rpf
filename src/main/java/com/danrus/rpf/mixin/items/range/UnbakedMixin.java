package com.danrus.rpf.mixin.items.range;

import com.danrus.rpf.RpfCodecs;
import com.danrus.rpf.api.DelegateItemModel;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RangeSelectItemModel.Unbaked.class)
public class UnbakedMixin implements DelegateItemModel.Unbaked {

    @Unique
    private boolean rpf$doDelegate = true;

    @Inject(
            method = "type",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void rpf$type(CallbackInfoReturnable<MapCodec<RangeSelectItemModel.Unbaked>> cir) {
        cir.setReturnValue(RpfCodecs.MAP_CODEC_RANGE);
    }

    @Override
    public boolean rpf$getDelegation() {
        return rpf$doDelegate;
    }

    @Override
    public void rpf$setDeligation(boolean value) {
        rpf$doDelegate = value;
    }
}
