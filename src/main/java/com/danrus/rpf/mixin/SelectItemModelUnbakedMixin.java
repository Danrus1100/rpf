package com.danrus.rpf.mixin;

import com.danrus.rpf.duck.RpfItemModel;
import com.danrus.rpf.duck.RpfSelectItemModel;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(SelectItemModel.Unbaked.class)
public class SelectItemModelUnbakedMixin implements RpfSelectItemModel.Unbaked {
    @Unique
    private static final MapCodec<SelectItemModel.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(
            (instance) -> instance.group(
                    SelectItemModel.UnbakedSwitch.MAP_CODEC.forGetter(SelectItemModel.Unbaked::unbakedSwitch),
                            ItemModels.CODEC.optionalFieldOf("fallback").forGetter(SelectItemModel.Unbaked::fallback),
                            Codec.BOOL.optionalFieldOf("delegate", true).forGetter((model) -> {
                                return ((RpfSelectItemModel.Unbaked) model).rpf$delegate();
                            })
                    ).apply(instance, ((unbakedSwitch, fallback, delegate) -> {
                       ItemModel.Unbaked model = new SelectItemModel.Unbaked(unbakedSwitch, fallback);
                       ((RpfSelectItemModel.Unbaked) model).rpf$setDeligation(delegate);
                       return model;
            })));

    @Unique
    private boolean rpf$doDelegate = true;


    @Inject(
            method = "bake",
            at = @At("RETURN")
    )
    private void rpf$setFallbackModel(ItemModel.BakingContext context, CallbackInfoReturnable<ItemModel> cir, @Local ItemModel itemModel) {
        ((RpfItemModel) itemModel).rpf$setFallback();
    }


    @Inject(
            method = "type",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void rpf$type(CallbackInfoReturnable<Codec<? extends SelectItemModel.Unbaked>> cir) {
        cir.setReturnValue(MAP_CODEC);
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
