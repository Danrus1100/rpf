package com.danrus.rpf.impl;

import com.danrus.rpf.Rpf;
import com.danrus.rpf.api.RpfItemModel;
import com.danrus.rpf.api.RpfItemModelResolver;
import com.danrus.rpf.api.event.RpfEvent;
import com.danrus.rpf.api.event.type.PreModelResolveEvent;
import com.danrus.rpf.core.item.ModelUpdateContext;
import com.danrus.rpf.core.item.SignedItemModel;
import com.danrus.rpf.duck.load.RpfModelManager;
import com.danrus.rpf.api.TestsResultCollector;
import com.danrus.rpf.debug.LoggingTestsResultCollector;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class RpfV1ModelResolver implements RpfItemModelResolver {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RpfV1ModelResolver.class);

    private final Map<DataComponentMap, ClientItem.Properties> componentsToProperties = 
        Collections.synchronizedMap(new WeakHashMap<>());
    private static final TestsResultCollector DUMMY_COLLECTOR = new DummyTestsResultsCollector();

    @Override
    public void resolveAndAppendLayer(ModelUpdateContext context, ItemStack stack, LivingEntity entity, Operation<Void> vanilla) {

        RpfModelManager rpfModelManager = RpfItemModelResolver.getModelManager();
        List<Map<ResourceLocation, SignedItemModel>> packs = rpfModelManager.rpf$getSignedModels();

        List<SignedItemModel> candidates = new ArrayList<>();
        for (Map<ResourceLocation, SignedItemModel> currentPack : packs) {
            SignedItemModel model = currentPack.get(context.location());
            if (model != null) {
                candidates.add(model);
            }
        }

        TestsResultCollector collector = Rpf.debug ? new LoggingTestsResultCollector(context.location(), candidates.getFirst().name()) : DUMMY_COLLECTOR;

        RpfEvent preEvent = new PreModelResolveEvent(context, stack, candidates, collector, entity);
        Rpf.getEventBus().post(preEvent);
        if (preEvent.isCancelled()) {
            return;
        }
        for (int i = 0; i < candidates.size(); i++) {
            try {
                SignedItemModel model = candidates.get(i);
                collector.resetShift();
                collector.pushPack(model.name());

                if (!(model.model() instanceof RpfItemModel)) {
                    model.update(context, stack, entity);
                    return;
                }

                if (!model.doDelegate(context, stack, entity, collector) || i == candidates.size() - 1) {
                    RpfItemModelResolver.appendModelLayer(context, stack, entity, componentsToProperties, model);
                    if (Rpf.debug) {
                        Rpf.getItemLogger().info(collector);
                    }
                    return;
                }
            } catch (Exception e) {
                // Log exception with context for debugging
                LOGGER.error(
                    "Exception while resolving model '{}' from pack '{}': {}",
                    context.location(),
                    i < candidates.size() ? candidates.get(i).name() : "unknown",
                    e.getMessage(),
                    e
                );
                
                // Continue to next candidate (fallback behavior)
                // If this was the last candidate, will fall through to updateMissingModel
            }

        }

        RpfItemModelResolver.updateMissingModel(context, collector, stack, entity);
    }

    @Override
    public boolean shouldPlaySwapAnimation(ItemStack stack, Operation<Boolean> vanilla) {
        ResourceLocation resourceLocation = stack.get(DataComponents.ITEM_MODEL);
        ClientItem.Properties properties = this.componentsToProperties.get(stack.getComponents()); // FIXME: not the best way to get properties
        if (resourceLocation == null || properties == null) {
            return true;
        };
        return properties.handAnimationOnSwap();
    }

}
