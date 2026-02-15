package com.danrus.rpf.api.event.type;

import com.danrus.rpf.api.event.AbstractModelResolverEvent;
import com.danrus.rpf.api.event.RpfEvent;
import com.danrus.rpf.core.SignedItemModel;
import com.danrus.rpf.logging.ModelTestsResultCollector;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PreModelResolveEvent extends AbstractModelResolverEvent {
    private final ResourceLocation modelId;
    private final List<SignedItemModel> candidates;
    private final ModelTestsResultCollector collector;

    public PreModelResolveEvent(ResourceLocation modelId, List<SignedItemModel> candidates, ModelTestsResultCollector collector, ItemStackRenderState renderState, ItemStack stack, ItemModelResolver itemModelResolver, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed) {
        super(renderState, stack, itemModelResolver, displayContext, level, owner, seed);
        this.modelId = modelId;
        this.candidates = candidates;
        this.collector = collector;
    }

    public ResourceLocation getModelId() {
        return modelId;
    }

    public List<SignedItemModel> getCandidates() {
        return candidates;
    }

    public ModelTestsResultCollector getCollector() {
        return collector;
    }
}
