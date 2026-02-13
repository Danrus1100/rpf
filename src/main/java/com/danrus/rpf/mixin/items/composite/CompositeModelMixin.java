package com.danrus.rpf.mixin.items.composite;

import com.danrus.rpf.api.RpfItemModel;
import com.danrus.rpf.duck.item.RpfCompositeModel;
import com.danrus.rpf.logging.ModelTestsResultCollector;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.CompositeModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
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
    public boolean rpf$doDelegate(ItemStackRenderState renderState, ItemStack stack, ItemModelResolver itemModelResolver, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable LivingEntity owner, @Nullable ItemModel prev, int seed, ResourceLocation itemModelId, String packName, ModelTestsResultCollector collector) {
        if (rpf$isFallback()) {
            models.forEach(model ->  ((RpfItemModel) model).rpf$markAsFallback());
        }
        boolean delegate = rpf$getDelegationInitialState();
        collector.touchInfo(this.getClass().getSimpleName() + " models: " + models.size(), packName, itemModelId);
        collector.pushShift();
        for (ItemModel model : models) {
            collector.touchInfo("Testing model: " + model.getClass().getSimpleName(), packName, itemModelId);
            if (((RpfItemModel)model).rpf$doDelegate(renderState, stack, itemModelResolver, displayContext, level, owner, prev, seed, itemModelId, packName, collector)) {
                collector.touchInfo("Model " + model.getClass().getSimpleName() + " cancel delegate", packName, itemModelId);
                delegate = rpf$getDelegationStateWhenDelegate();
            }
        }

        StringBuilder modesString = new StringBuilder();
        models.forEach(m -> {
            modesString.append(m.getClass().getSimpleName() + ", ");
        });

        if (delegate) {
            collector.touchDelegate(this.getClass().getSimpleName() + " models: " + models.size() + ": " + modesString, packName, itemModelId);
        } else {
            collector.touchAllow(this.getClass().getSimpleName() + " models: " + models.size() + ": " + modesString, packName, itemModelId);
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
