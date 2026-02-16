package com.danrus.rpf.api.event;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractModelResolverEvent extends RpfEvent{
    private final ItemStackRenderState renderState;
    private final ItemStack stack;
    private final ItemModelResolver itemModelResolver;
    private final ItemDisplayContext displayContext;
    @Nullable
    private final ClientLevel level;
    @Nullable
    private final LivingEntity owner;
    private final int seed;

    public AbstractModelResolverEvent(ItemStackRenderState renderState, ItemStack stack, ItemModelResolver itemModelResolver, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed) {
        this.renderState = renderState;
        this.stack = stack;
        this.itemModelResolver = itemModelResolver;
        this.displayContext = displayContext;
        this.level = level;
        this.owner = owner;
        this.seed = seed;
    }

    public ItemStackRenderState getRenderState() {
        return renderState;
    }
    public ItemStack getStack() {
        return stack;
    }
    public ItemModelResolver getItemModelResolver() {
        return itemModelResolver;
    }
    public ItemDisplayContext getDisplayContext() {
        return displayContext;
    }
    @Nullable
    public ClientLevel getLevel() {
        return level;
    }
    @Nullable
    public LivingEntity getOwner() {
        return owner;
    }
    public int getSeed() {
        return seed;
    }
}
