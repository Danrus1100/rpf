package com.danrus.rpf.mixin.items.block;

import com.danrus.rpf.api.RpfItemModel;
import com.danrus.rpf.duck.item.RpfBlockModelWrapper;
import com.danrus.rpf.logging.ModelTestsResultCollector;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockModelWrapper.class)
public abstract class BlockModelWrapperMixin implements RpfItemModel, RpfBlockModelWrapper {

    @Unique private Identifier rpf$modelLink;

    public void rpf$setModelLink(Identifier location) { this.rpf$modelLink = location; }
    public Identifier rpf$getModelLink() { return this.rpf$modelLink; }

    @Override
    public boolean rpf$testForDelegate(
            ItemStackRenderState renderState,
            ItemStack stack,
            ItemModelResolver itemModelResolver,
            ItemDisplayContext displayContext,
            @Nullable ClientLevel level,
            @Nullable ItemOwner owner,
            int seed,
            Identifier itemModelId,
            String packName,
            ModelTestsResultCollector collector
    ){
        boolean delegate = this.rpf$isFallback()
                
                // try to predict is this model from vanilla resources
                && this.rpf$modelLink.getNamespace().equals(itemModelId.getNamespace())
                && this.rpf$modelLink.getPath().contains(itemModelId.getPath());
        if (delegate) {
            collector.touchDelegate(this.getClass().getSimpleName() + ": " + rpf$getModelLink().toString(), packName, itemModelId);
        } else {
            collector.touchAllow(this.getClass().getSimpleName() + ": " + rpf$getModelLink().toString(), packName, itemModelId);
        }

        return delegate;
    }
}
