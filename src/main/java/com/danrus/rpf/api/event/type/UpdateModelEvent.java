package com.danrus.rpf.api.event.type;

import com.danrus.rpf.api.event.AbstractModelResolverEvent;
import com.danrus.rpf.core.item.SignedItemModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class UpdateModelEvent extends AbstractModelResolverEvent {
    private final SignedItemModel model;

    public UpdateModelEvent(SignedItemModel model, ItemStackRenderState renderState, ItemStack stack, ItemModelResolver itemModelResolver, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed) {
        super(renderState, stack, itemModelResolver, displayContext, level, owner, seed);
        this.model = model;
    }

    public SignedItemModel getModel() {
        return model;
    }
}
