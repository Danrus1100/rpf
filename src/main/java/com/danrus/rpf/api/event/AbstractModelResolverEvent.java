package com.danrus.rpf.api.event;

import com.danrus.rpf.core.item.ModelUpdateContext;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractModelResolverEvent extends RpfEvent{
    private final ModelUpdateContext context;
    private final ItemStack stack;
    @Nullable
    private final ItemOwner owner;

    public AbstractModelResolverEvent(ModelUpdateContext context, ItemStack stack, @Nullable ItemOwner owner) {
        this.context = context;
        this.stack = stack;
        this.owner = owner;
    }

    public ModelUpdateContext getContext() {
        return context;
    }

    public ItemStack getStack() {
        return stack;
    }
    @Nullable
    public ItemOwner getOwner() {
        return owner;
    }
}
