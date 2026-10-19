package com.danrus.rpf.mixin.items.composite;

import com.danrus.rpf.api.RpfItemModel;
import com.danrus.rpf.core.item.ModelUpdateContext;
import com.danrus.rpf.duck.item.RpfCompositeModel;
import com.danrus.rpf.api.TestsResultCollector;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.CompositeModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(CompositeModel.class)
public abstract class CompositeModelMixin implements RpfItemModel, RpfCompositeModel {

    RpfCompositeModel.DelegateStrategy rpf$delegateStrategy = DelegateStrategy.ONE_DO_DELEGATE;

    @Shadow
    @Final
    private List<ItemModel> models;

    @Override
    public DelegateStrategy rpf$getDelegateStrategy() {
        return rpf$delegateStrategy;
    }

    @Override
    public void rpf$setDelegateStrategy(DelegateStrategy strategy) {
        this.rpf$delegateStrategy = strategy;
    }

    @Override
    public boolean rpf$doDelegate(ModelUpdateContext context, ItemStack stack, @Nullable ItemOwner owner, @Nullable ItemModel prev, TestsResultCollector collector) {
        if (rpf$isFallback()) {
            models.forEach(model ->  ((RpfItemModel) model).rpf$markAsFallback());
        }
        boolean delegate = rpf$getDelegationInitialState();
        collector.info(this.getClass(), " models: " + models.size());
        collector.pushShift();
        for (ItemModel model : models) {
            collector.info("Testing model: " + model.getClass().getSimpleName());
            if (((RpfItemModel)model).rpf$doDelegate(context, stack, owner, (ItemModel) (Object) this, collector)) {
                collector.info("Model " + model.getClass().getSimpleName() + " cancel delegate");
                delegate = rpf$getDelegationStateWhenDelegate();
            }
        }

        StringBuilder modesString = new StringBuilder();
        models.forEach(m -> {
            modesString.append(m.getClass().getSimpleName() + ", ");
        });

        if (delegate) {
            collector.delegate(this.getClass(), " models: " + models.size() + ": " + modesString);
        } else {
            collector.hit(this.getClass(), " models: " + models.size() + ": " + modesString);
        }
        collector.popShift();
        return delegate;
    }

    @Unique
    private boolean rpf$getDelegationInitialState() {
        return switch (rpf$delegateStrategy) {
            case ONE_CANCEL_DELEGATE -> true;
            case ONE_DO_DELEGATE, NOT_DELEGATE -> false;
        };
    }

    @Unique
    private boolean rpf$getDelegationStateWhenDelegate() {
        return switch (rpf$delegateStrategy) {
            case ONE_DO_DELEGATE -> true;
            case ONE_CANCEL_DELEGATE, NOT_DELEGATE -> false;
        };
    }

}
