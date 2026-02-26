package com.danrus.rpf.api.event.type;

import com.danrus.rpf.api.event.AbstractModelResolverEvent;
import com.danrus.rpf.api.TestsResultCollector;
import com.danrus.rpf.core.item.ModelUpdateContext;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class MissingModelUpdateEvent extends AbstractModelResolverEvent {
    private final TestsResultCollector collector;

    public MissingModelUpdateEvent(ModelUpdateContext context, ItemStack stack, @Nullable ItemOwner owner, TestsResultCollector collector) {
        super(context, stack, owner);
        this.collector = collector;
    }

    public TestsResultCollector getCollector() {
        return collector;
    }
}
