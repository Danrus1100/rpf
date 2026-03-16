package com.danrus.rpf.core.item;

import com.danrus.rpf.Rpf;
import com.danrus.rpf.api.RpfItemModel;
import com.danrus.rpf.api.event.RpfEvent;
import com.danrus.rpf.api.event.type.UpdateModelEvent;
import com.danrus.rpf.api.TestsResultCollector;
import com.danrus.rpf.debug.RpfDebugSystem;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record SignedItemModel(
        String name,
        ItemModel model
) {
    public boolean doDelegate(
            ModelUpdateContext context,
            ItemStack stack,
            @Nullable LivingEntity owner,
            TestsResultCollector collector
    ) {
        if (model == null) return false;
        try {
            return ((RpfItemModel)model).rpf$doDelegate(
                context,
                stack,
                owner,
                null,
                collector
            );
        } catch (Exception e) {
            RpfDebugSystem.getInstance().errorItem(collector);
            return true;
        }
    }

    public void update(ModelUpdateContext context, ItemStack stack, @Nullable LivingEntity owner) {
        if (model != null) {
            RpfEvent event = new UpdateModelEvent(this, context, stack, owner);
            Rpf.getEventBus().post(event);
            if (event.isCancelled()) return;
            model.update(context.renderState(), stack, context.mcResolver(), context.displayContext(), context.level(), owner, context.seed());
        }
    }

}
