//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.danrus.rpf.impl;

import com.danrus.rpf.Rpf;
import com.danrus.rpf.api.RpfItemModel;
import com.danrus.rpf.api.RpfItemModelResolver;
import com.danrus.rpf.api.TestsResultCollector;
import com.danrus.rpf.api.event.RpfEvent;
import com.danrus.rpf.api.event.type.PreModelResolveEvent;
import com.danrus.rpf.core.item.ModelUpdateContext;
import com.danrus.rpf.core.item.SignedItemModel;
import com.danrus.rpf.debug.LoggingTestsResultCollector;
import com.danrus.rpf.debug.RpfDebugSystem;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpfV1ModelResolver extends AbstractResolverWithCandidates {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpfV1ModelResolver.class);
    private final Map<DataComponentMap, ClientItem.Properties> componentsToProperties = Collections.synchronizedMap(new WeakHashMap());

    public void resolveAndAppendLayer(ModelUpdateContext context, List<SignedItemModel> candidates, ItemStack stack, LivingEntity entity, Operation<Void> vanilla) {
        TestsResultCollector collector = RpfDebugSystem.getInstance().optimiseCollector(() -> new LoggingTestsResultCollector(context.location(), ((SignedItemModel)candidates.getFirst()).name()));
        RpfEvent preEvent = new PreModelResolveEvent(context, stack, candidates, collector, entity);
        Rpf.getEventBus().post(preEvent);
        if (!preEvent.isCancelled()) {
            for(int i = 0; i < candidates.size(); ++i) {
                try {
                    SignedItemModel model = (SignedItemModel)candidates.get(i);
                    collector.resetShift();
                    collector.pushPack(model.name());
                    if (!(model.model() instanceof RpfItemModel)) {
//                        model.update(context, stack, entity);
                        vanilla.call(context.renderState(), stack, context.displayContext(), context.level(), entity, context.seed());
                        return;
                    }

                    if (!model.doDelegate(context, stack, entity, collector) || i == candidates.size() - 1) {
                        RpfItemModelResolver.appendModelLayer(context, stack, entity, this.componentsToProperties, model);
                        RpfDebugSystem.getInstance().logItem(collector);
                        return;
                    }
                } catch (Exception e) {
                    LOGGER.error("Exception while resolving model '{}' from pack '{}': {}", new Object[]{context.location(), i < candidates.size() ? ((SignedItemModel)candidates.get(i)).name() : "unknown", e.getMessage(), e});
                }
            }

            vanilla.call(context.renderState(), stack, context.displayContext(), context.level(), entity, context.seed());
//            RpfItemModelResolver.updateMissingModel(context, collector, stack, entity);
        }
    }

    public boolean shouldPlaySwapAnimation(ItemStack stack, Operation<Boolean> vanilla) {
        ResourceLocation resourceLocation = (ResourceLocation)stack.get(DataComponents.ITEM_MODEL);
        ClientItem.Properties properties = (ClientItem.Properties)this.componentsToProperties.get(stack.getComponents());
        return resourceLocation != null && properties != null ? properties.handAnimationOnSwap() : true;
    }
}
