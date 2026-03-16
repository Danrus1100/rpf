package com.danrus.rpf.mixin.items.range;

import com.danrus.rpf.api.DelegateItemModel;
import com.danrus.rpf.api.RpfItemModel;
import com.danrus.rpf.api.TestsResultCollector;
import com.danrus.rpf.core.item.ModelUpdateContext;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(RangeSelectItemModel.class)
public abstract class RangeSelectItemModelMixin implements RpfItemModel, DelegateItemModel {
    @Shadow
    @Final
    private ItemModel fallback;
    @Shadow
    @Final
    private RangeSelectItemModelProperty property;
    @Shadow
    @Final
    private float scale;
    @Shadow
    @Final
    private float[] thresholds;
    @Shadow
    @Final
    private ItemModel[] models;
    @Unique
    boolean rpf$delegate = true;

    @Override
    public boolean rpf$getDelegation() {
        return rpf$delegate;
    }

    @Override
    public void rpf$setDeligation(boolean value) {
        this.rpf$delegate = value;
    }

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void rpf$init(RangeSelectItemModelProperty property, float scale, float[] thresholds, ItemModel[] models, ItemModel fallback, CallbackInfo ci){
        ((RpfItemModel) this.fallback).rpf$markAsFallback();
    }

    @Override
    public boolean rpf$doDelegate(ModelUpdateContext context, ItemStack stack, @Nullable LivingEntity owner, @Nullable ItemModel prev, TestsResultCollector collector) {
        if (!this.rpf$delegate) {
            collector.hit(this.getClass(), " force cancel delegate");
            return false;
        }
        if (prev != null && this.rpf$isFallback()) {
            Arrays.stream(models).forEach(model -> ((RpfItemModel) model).rpf$markAsFallback());
        }
        float f = property.get(stack, context.level(), owner, context.seed()) * scale;
        boolean isFallback = false;
        ItemModel itemModel;
        if (Float.isNaN(f)) {
            itemModel = this.fallback;
            isFallback = true;
        } else {
            int i = RangeSelectItemModel.lastIndexLessOrEqual(thresholds, f);
            itemModel = i == -1 ? this.fallback : models[i];
        }
        if (!(itemModel instanceof RpfItemModel)) {
            return this.rpf$getDelegation();
        }
        collector.next(this.getClass(),": property " + property.toString() + ", value " + f, isFallback);
        return (((RpfItemModel) itemModel).rpf$doDelegate(context, stack, owner, (ItemModel) (Object) this, collector));
    }
}
