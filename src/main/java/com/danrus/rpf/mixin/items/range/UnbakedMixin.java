package com.danrus.rpf.mixin.items.range;

import com.danrus.rpf.RpfCodecs;
import com.danrus.rpf.api.DelegateItemModel;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RangeSelectItemModel.Unbaked.class)
public class UnbakedMixin implements DelegateItemModel.Unbaked {

    @Unique
    private boolean rpf$doDelegate = true;

    @Override
    public boolean rpf$getDelegation() {
        return rpf$doDelegate;
    }

    @Override
    public void rpf$setDeligation(boolean value) {
        rpf$doDelegate = value;
    }
}
