package com.danrus.rpf.core.item;

import com.danrus.rpf.Rpf;
import com.danrus.rpf.api.RpfItemModel;
import com.danrus.rpf.api.event.RpfEvent;
import com.danrus.rpf.api.event.type.UpdateModelEvent;
import com.danrus.rpf.api.TestsResultCollector;
import com.danrus.rpf.debug.RpfDebugSystem;
import com.danrus.rpf.debug.RpfLogger;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record SignedItemModel(
        PackLocationInfo info,
        ItemModel model
) {
    public boolean doDelegate(
            ModelUpdateContext context,
            ItemStack stack,
            @Nullable ItemOwner owner,
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
        } catch (Exception exception) {
            String modelName = info() == null ? "unknown" : info().id();
            RpfLogger.get().error("Failed to resolve delegation for model '" + modelName + "'", exception);
            RpfDebugSystem.getInstance().errorItem(collector);
            return true;
        }
    }

    public void update(ModelUpdateContext context, ItemStack stack, @Nullable ItemOwner owner) {
        if (model != null) {
            RpfEvent event = new UpdateModelEvent(this, context, stack, owner);
            Rpf.getEventBus().post(event);
            if (event.isCancelled()) return;
            model.update(context.renderState(), stack, context.mcResolver(), context.displayContext(), context.level(), owner, context.seed());
        }
    }

    public String name() {
        return info().id();
    }

}
