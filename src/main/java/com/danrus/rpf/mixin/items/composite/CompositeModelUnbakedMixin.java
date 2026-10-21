package com.danrus.rpf.mixin.items.composite;

import com.danrus.rpf.duck.item.RpfCompositeModel;
import net.minecraft.client.renderer.item.CompositeModel;
import net.minecraft.client.renderer.item.ItemModel;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CompositeModel.Unbaked.class)
public class CompositeModelUnbakedMixin implements RpfCompositeModel.Unbaked {

    @Unique
    private RpfCompositeModel.DelegateStrategy rpf$delegateStrategy = RpfCompositeModel.DelegateStrategy.ONE_DO_DELEGATE;

    @Inject(
            method = "bake",
            at = @At("RETURN")
    )
    private void rpf$applyDelegateStrategy(
            ItemModel.BakingContext context,
            Matrix4fc transformation,
            CallbackInfoReturnable<ItemModel> cir
    ) {
        if (cir.getReturnValue() instanceof RpfCompositeModel composite) {
            composite.rpf$setDelegateStrategy(this.rpf$delegateStrategy);
        }
    }

    @Override
    public void rpf$setDelegateStrategy(RpfCompositeModel.DelegateStrategy strategy) {
        this.rpf$delegateStrategy = strategy;
    }

    @Override
    public RpfCompositeModel.DelegateStrategy rpf$getDelegateStrategy() {
        return this.rpf$delegateStrategy;
    }
}
