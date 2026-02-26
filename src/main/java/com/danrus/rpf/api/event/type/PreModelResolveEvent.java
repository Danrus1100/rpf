package com.danrus.rpf.api.event.type;

import com.danrus.rpf.api.event.AbstractModelResolverEvent;
import com.danrus.rpf.core.item.ModelUpdateContext;
import com.danrus.rpf.core.item.SignedItemModel;
import com.danrus.rpf.api.TestsResultCollector;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PreModelResolveEvent extends AbstractModelResolverEvent {
    private final List<SignedItemModel> candidates;
    private final TestsResultCollector collector;

    public PreModelResolveEvent(ModelUpdateContext context, ItemStack stack, List<SignedItemModel> candidates, TestsResultCollector collector, @Nullable ItemOwner owner) {
        super(context, stack, owner);
        this.candidates = candidates;
        this.collector = collector;
    }

    public List<SignedItemModel> getCandidates() {
        return candidates;
    }

    public TestsResultCollector getCollector() {
        return collector;
    }
}
