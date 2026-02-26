package com.danrus.rpf.item.items;

import com.danrus.rpf.api.RpfItemModel;
import com.danrus.rpf.api.TestsResultCollector;
import com.danrus.rpf.core.item.ModelUpdateContext;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record TestItemModel(boolean delegate, boolean fallback) implements RpfItemModel, ItemModel {
    @Override
    public void rpf$markAsFallback() {}

    @Override
    public boolean rpf$isFallback() {
        return fallback;
    }

    @Override
    public boolean rpf$doDelegate(ModelUpdateContext context, ItemStack stack, @Nullable ItemOwner owner, @Nullable ItemModel prev, TestsResultCollector collector) {
        return delegate;
    }

    @Override
    public void update(ItemStackRenderState renderState, ItemStack stack, ItemModelResolver itemModelResolver, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable ItemOwner entity, int seed) {
        if (delegate) throw new IllegalStateException("TestModel is marked as delegate, but update() was called");
    }

    public record Unbaked(boolean delegate, boolean fallback) implements ItemModel.Unbaked {

        @Override
        public MapCodec<? extends ItemModel.Unbaked> type() {
            return null;
        }

        @Override
        public @NotNull ItemModel bake(BakingContext context) {
            return new TestItemModel(delegate, fallback);
        }

        @Override
        public void resolveDependencies(Resolver resolver) {

        }
    }
}
