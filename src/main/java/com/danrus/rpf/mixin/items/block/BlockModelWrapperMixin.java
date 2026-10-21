package com.danrus.rpf.mixin.items.block;

import com.danrus.rpf.api.RpfItemModel;
import com.danrus.rpf.core.item.ModelUpdateContext;
import com.danrus.rpf.duck.item.RpfBlockModelWrapper;
import com.danrus.rpf.api.TestsResultCollector;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CuboidItemModelWrapper.class)
public abstract class BlockModelWrapperMixin implements RpfItemModel, RpfBlockModelWrapper {

    @Unique private Identifier rpf$modelLink;

    public void rpf$setModelLink(Identifier location) { this.rpf$modelLink = location; }
    public Identifier rpf$getModelLink() { return this.rpf$modelLink; }

    @Override
    public boolean rpf$doDelegate(ModelUpdateContext context, ItemStack stack, @Nullable ItemOwner owner, @Nullable ItemModel prev, TestsResultCollector collector){
        Identifier modelLink = this.rpf$modelLink;
        // Vanilla links an item model "ns:path" to the block model "ns:item/path".
        // Compare exactly: "contains" would also match "item/path_alt".
        boolean delegate = this.rpf$isFallback()
                && modelLink != null
                && modelLink.equals(context.location().withPrefix("item/"));
        String link = modelLink == null ? "null" : modelLink.toString();
        if (delegate) {
            collector.delegate(this.getClass(), ": " + link);
        } else {
            collector.hit(this.getClass(), ": " + link);
        }

        return delegate;
    }
}
