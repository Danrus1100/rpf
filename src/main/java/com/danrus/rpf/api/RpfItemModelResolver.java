package com.danrus.rpf.api;

import com.danrus.rpf.Rpf;
import com.danrus.rpf.api.event.RpfEvent;
import com.danrus.rpf.api.event.type.MissingModelUpdateEvent;
import com.danrus.rpf.core.item.ModelUpdateContext;
import com.danrus.rpf.core.item.RpfModelIdentity;
import com.danrus.rpf.core.item.SignedItemModel;
import com.danrus.rpf.duck.load.RpfModelManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface RpfItemModelResolver {
    void resolveAndAppendLayer(
            ModelUpdateContext context,
            ItemStack stack,
            LivingEntity entity,
            Operation<Void> vanilla
    );

    boolean shouldPlaySwapAnimation(ItemStack stack, Operation<Boolean> vanilla);

    public static RpfModelManager getModelManager() {
        return (RpfModelManager) Minecraft.getInstance().getModelManager();
    }

    public static void appendModelLayer(ModelUpdateContext context, ItemStack stack, LivingEntity entity, SignedItemModel model) {
        appendModelLayer(context, stack, entity, null, model);
    }

    public static void appendModelLayer(ModelUpdateContext context, ItemStack stack, LivingEntity entity, @Nullable Map<DataComponentMap, ClientItem.Properties> componentsToProperties, SignedItemModel model) {
        RpfModelIdentity identity = new RpfModelIdentity(context.location(), model.name());
        ClientItem.Properties properties = getModelManager().rpf$getProperties(identity);
        context.renderState().setOversizedInGui(properties != null && properties.oversizedInGui());
        if (componentsToProperties != null) componentsToProperties.put(stack.getComponents(), properties);
        context.renderState().appendModelIdentityElement(identity); // for correct GUI rendering
        model.update(context, stack, entity);
    }

    public static void updateMissingModel(ModelUpdateContext context, TestsResultCollector collector, ItemStack stack, @Nullable LivingEntity owner){
        RpfEvent event = new MissingModelUpdateEvent(context, stack, owner, collector);
        Rpf.getEventBus().post(event);
        if (event.isCancelled()) return;
        context.renderState().appendModelIdentityElement(new RpfModelIdentity(context.location(), "Unknown")); // no model found
        getModelManager().rpf$getMissingModel().update(context.renderState(), stack, context.mcResolver(), context.displayContext(), context.level(), owner, context.seed());
        collector.touchModelNotFound();
        Rpf.getItemLogger().error(collector);
    }
}
