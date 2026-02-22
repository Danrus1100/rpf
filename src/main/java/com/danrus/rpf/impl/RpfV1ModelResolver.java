package com.danrus.rpf.impl;

import com.danrus.rpf.Rpf;
import com.danrus.rpf.api.RpfItemModel;
import com.danrus.rpf.api.RpfItemModelResolver;
import com.danrus.rpf.api.event.RpfEvent;
import com.danrus.rpf.api.event.type.MissingModelUpdateEvent;
import com.danrus.rpf.api.event.type.PreModelResolveEvent;
import com.danrus.rpf.core.item.RpfModelIdentity;
import com.danrus.rpf.core.item.SignedItemModel;
import com.danrus.rpf.duck.load.RpfModelManager;
import com.danrus.rpf.api.TestsResultCollector;
import com.danrus.rpf.logging.DummyTestsResultsCollector;
import com.danrus.rpf.logging.LoggingTestsResultCollector;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class RpfV1ModelResolver implements RpfItemModelResolver {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RpfV1ModelResolver.class);

    // WeakHashMap allows garbage collection of entries when ItemStacks are no longer referenced
    // Collections.synchronizedMap ensures thread safety during concurrent access
    private final Map<DataComponentMap, ClientItem.Properties> componentsToProperties = 
        Collections.synchronizedMap(new WeakHashMap<>());
    private static final TestsResultCollector DUMMY_COLLECTOR = new DummyTestsResultsCollector();

    @Override
    public void resolveAndAppendLayer(ItemStackRenderState renderState, ItemStack stack, ItemDisplayContext displayContext, Level level, LivingEntity entity, int seed, Operation<Void> original, ItemModelResolver mcResolver) {
        ResourceLocation resourceLocation = stack.get(DataComponents.ITEM_MODEL);
        if (resourceLocation == null) return;

        ClientLevel clientLevel = level instanceof ClientLevel cl ? cl : null;

        TestsResultCollector collector = Rpf.debug ? new LoggingTestsResultCollector(resourceLocation) : DUMMY_COLLECTOR;

        RpfModelManager rpfModelManager = RpfItemModelResolver.getModelManager();
        List<Map<ResourceLocation, SignedItemModel>> packs = rpfModelManager.rpf$getSignedModels();

        List<SignedItemModel> candidates = new ArrayList<>();
        for (Map<ResourceLocation, SignedItemModel> currentPack : packs) {
            SignedItemModel model = currentPack.get(resourceLocation);
            if (model != null) {
                candidates.add(model);
            }
        }

        RpfEvent preEvent = new PreModelResolveEvent(resourceLocation, candidates, collector, renderState, stack, mcResolver, displayContext, clientLevel, entity, seed);
        Rpf.getEventBus().post(preEvent);
        if (preEvent.isCancelled()) {
            return;
        }
        for (int i = 0; i < candidates.size(); i++) {
            try {
                SignedItemModel model = candidates.get(i);
                collector.resetShift();

                if (!(model.model() instanceof RpfItemModel)) {
                    model.model().update(renderState, stack, mcResolver, displayContext, clientLevel, entity, seed);
                    return;
                }

                if (!model.doDelegate(renderState, stack, mcResolver, displayContext, clientLevel, entity, seed, resourceLocation, collector) || i == candidates.size() - 1) {
                    RpfModelIdentity identity = new RpfModelIdentity(resourceLocation, model.name());
                    ClientItem.Properties properties = rpfModelManager.rpf$getProperties(identity);
                    if (properties == null) {
                        properties = ClientItem.Properties.DEFAULT;
                    }
                    renderState.setOversizedInGui(properties.oversizedInGui());
                    this.componentsToProperties.put(stack.getComponents(), properties);
                    renderState.appendModelIdentityElement(identity); // for correct GUI rendering
                    model.update(renderState, stack, mcResolver, displayContext, clientLevel, entity, seed);
                    if (Rpf.debug) {
                        Rpf.getItemLogger().info(collector);
                    }
                    return;
                }
            } catch (Exception e) {
                // Log exception with context for debugging
                LOGGER.error(
                    "Exception while resolving model '{}' from pack '{}': {}",
                    resourceLocation,
                    i < candidates.size() ? candidates.get(i).name() : "unknown",
                    e.getMessage(),
                    e
                );
                
                // Continue to next candidate (fallback behavior)
                // If this was the last candidate, will fall through to updateMissingModel
            }

        }

        updateMissingModel(resourceLocation, rpfModelManager, renderState, collector, stack, displayContext, clientLevel, entity, seed, mcResolver);
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

    public static void updateMissingModel(ResourceLocation resourceLocation, RpfModelManager modelManager, ItemStackRenderState renderState, TestsResultCollector collector, ItemStack stack, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed, ItemModelResolver resolver){
        RpfEvent event = new MissingModelUpdateEvent(resourceLocation, renderState, stack, resolver, displayContext, level, owner, seed, collector);
        Rpf.getEventBus().post(event);
        if (event.isCancelled()) return;
        renderState.appendModelIdentityElement(new RpfModelIdentity(resourceLocation, "Unknown")); // no model found
        modelManager.rpf$getMissingModel().update(renderState, stack, resolver, displayContext, level, owner, seed);
        collector.touchModelNotFound();
        Rpf.getItemLogger().error(collector);
    }
}
