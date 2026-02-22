package com.danrus.rpf.api.event.type;

import com.danrus.rpf.api.event.AbstractModelResolverEvent;
import com.danrus.rpf.api.TestsResultCollector;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class MissingModelUpdateEvent extends AbstractModelResolverEvent {
    private final TestsResultCollector collector;
    private final ResourceLocation modelId; // For consistency with PreModelResolveEvent, even though it's not used here.
    public MissingModelUpdateEvent(ResourceLocation location, ItemStackRenderState renderState, ItemStack stack, ItemModelResolver itemModelResolver, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed, TestsResultCollector collector) {
        super(renderState, stack, itemModelResolver, displayContext, level, owner, seed);
        this.collector = collector;
        this.modelId = location;
    }

    public TestsResultCollector getCollector() {
        return collector;
    }

    public ResourceLocation getModelId() {
        return modelId;
    }
}
