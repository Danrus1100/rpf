package com.danrus.rpf.mixin;

import com.danrus.rpf.Rpf;
import com.danrus.rpf.core.RpfModelIdentity;
import com.danrus.rpf.api.RpfItemModel;
import com.danrus.rpf.core.SignedItemModel;
import com.danrus.rpf.duck.load.RpfModelManager;
import com.danrus.rpf.logging.ModelTestsResultCollector;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(ItemModelResolver.class)
public class ItemModelResolverMixin<T, R> {

    @Unique
    private final Map<DataComponentMap, ClientItem.Properties> componentsToProperties = new HashMap<>();

    @Inject(
            method = "appendItemLayers",
            at = @At("HEAD"),
            cancellable = true
    )
    private void rpf$selectModel(ItemStackRenderState renderState, ItemStack stack, ItemDisplayContext displayContext, Level level, LivingEntity entity, int seed, CallbackInfo ci) {
        ResourceLocation resourceLocation = stack.get(DataComponents.ITEM_MODEL);
        if (resourceLocation == null) return;

        ClientLevel clientLevel = level instanceof ClientLevel cl ? cl : null;

        ModelTestsResultCollector collector = new ModelTestsResultCollector();

        RpfModelManager rpfModelManager = (RpfModelManager) Minecraft.getInstance().getModelManager();
        List<Map<ResourceLocation, SignedItemModel>> packs = rpfModelManager.rpf$getSignedModels();
        int packsCont = packs.size();

        for (int i = 0; i < packsCont; i++) {
            try {
                Map<ResourceLocation, SignedItemModel> currentPack = packs.get(i);
                SignedItemModel model = currentPack.get(resourceLocation);

                if (!(model.model() instanceof RpfItemModel)) {
                    model.model().update(renderState, stack, (ItemModelResolver) (Object) this, displayContext, clientLevel, entity, seed);
                    ci.cancel();
                    return;
                }

                if (!model.doDelegate(renderState, stack, (ItemModelResolver) (Object) this, displayContext, clientLevel, entity, seed, resourceLocation, collector) || i == packsCont - 1) {
                    ClientItem.Properties properties = rpfModelManager.rpf$getItemPropertiesMaps().get(i).get(resourceLocation);
                    if (properties == null) {
                        properties = ClientItem.Properties.DEFAULT;
                    }
                    renderState.setOversizedInGui(properties.oversizedInGui());
                    this.componentsToProperties.put(stack.getComponents(), properties);
                    renderState.appendModelIdentityElement(new RpfModelIdentity(resourceLocation, i, true)); // for correct GUI rendering
                    model.update(renderState, stack, (ItemModelResolver) (Object) this, displayContext, clientLevel, entity, seed);
                    if (Rpf.debug) {
                        Rpf.getItemLogger().info(collector);
                    }
                    ci.cancel();
                    return;
                }
            } catch (Exception e) {
//                e.printStackTrace(); //TODO: remove
            }

        }

        updateMissingModel(resourceLocation, rpfModelManager, renderState, collector, stack, displayContext, clientLevel, entity, seed);
        ci.cancel();
    }

    @Unique
    private void updateMissingModel(ResourceLocation resourceLocation, RpfModelManager modelManager, ItemStackRenderState renderState, ModelTestsResultCollector collector, ItemStack stack, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed){
        renderState.appendModelIdentityElement(new RpfModelIdentity(resourceLocation, -1, false)); // no model found
        modelManager.rpf$getMissingModel().update(renderState, stack, (ItemModelResolver) (Object) this, displayContext, level, owner, seed);
        collector.touchModelNotFound(resourceLocation);
        Rpf.getItemLogger().error(collector);
    }

    @Inject(
            method = "shouldPlaySwapAnimation",
            at = @At("HEAD"),
            cancellable = true
    )
    private void rpf$shouldPlaySwapAnimation(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        ResourceLocation resourceLocation = stack.get(DataComponents.ITEM_MODEL);
        ClientItem.Properties properties = this.componentsToProperties.get(stack.getComponents()); // FIXME: not the best way to get properties
        if (resourceLocation == null || properties == null) {
            cir.setReturnValue(true);
            return;
        };
        cir.setReturnValue(properties.handAnimationOnSwap());
    }
}
