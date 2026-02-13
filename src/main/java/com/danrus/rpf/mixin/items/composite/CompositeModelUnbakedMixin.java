package com.danrus.rpf.mixin.items.composite;

import com.danrus.rpf.duck.item.RpfCompositeModel;
import net.minecraft.client.renderer.item.CompositeModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CompositeModel.Unbaked.class)
public class CompositeModelUnbakedMixin implements RpfCompositeModel.Unbaked {

    @Unique
    private RpfCompositeModel.DelegateStrategy rpf$delegateStrategy = RpfCompositeModel.DelegateStrategy.ONE_DO_DELEGATE;

    @Override
    public void rpf$setDelegateStrategy(RpfCompositeModel.DelegateStrategy strategy) {
        this.rpf$delegateStrategy = strategy;
    }

    @Override
    public RpfCompositeModel.DelegateStrategy rpf$getDelegateStrategy() {
        return this.rpf$delegateStrategy;
    }
}
