package com.danrus.rpf.mixin.items.block;

import com.danrus.rpf.api.RpfItemModel;
import com.danrus.rpf.core.item.ModelUpdateContext;
import com.danrus.rpf.duck.item.RpfBlockModelWrapper;
import com.danrus.rpf.api.TestsResultCollector;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockModelWrapper.class)
public abstract class BlockModelWrapperMixin implements RpfItemModel, RpfBlockModelWrapper {

    @Unique private ResourceLocation rpf$modelLink;

    public void rpf$setModelLink(ResourceLocation location) { this.rpf$modelLink = location; }
    public ResourceLocation rpf$getModelLink() { return this.rpf$modelLink; }

    @Override
    public boolean rpf$doDelegate(ModelUpdateContext context, ItemStack stack, @Nullable LivingEntity owner, @Nullable ItemModel prev, TestsResultCollector collector){
        boolean delegate = this.rpf$isFallback()
                
                // try to predict is this model from vanilla resources
                && this.rpf$modelLink.getNamespace().equals(context.location().getNamespace())
                && this.rpf$modelLink.getPath().contains(context.location().getPath());
        if (delegate) {
            collector.delegate(this.getClass(), ": " + rpf$getModelLink().toString());
        } else {
            collector.hit(this.getClass(), ": " + rpf$getModelLink().toString());
        }

        return delegate;
    }
}
