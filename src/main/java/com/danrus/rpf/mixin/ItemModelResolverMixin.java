package com.danrus.rpf.mixin;

import com.danrus.rpf.RpfModelIdentity;
import com.danrus.rpf.api.RpfItemModel;
import com.danrus.rpf.duck.load.RpfModelManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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

        RpfModelManager rpfModelManager = (RpfModelManager) Minecraft.getInstance().getModelManager();
        List<Map<ResourceLocation, ItemModel>> packs = rpfModelManager.rpf$getModelMaps();
        int packsCont = packs.size();

        for (int i = 0; i < packsCont; i++) {
            Map<ResourceLocation, ItemModel> currentPack = packs.get(i);
            ItemModel model = currentPack.get(resourceLocation);

            if (!(model instanceof RpfItemModel) && model != null) {
                model.update(renderState, stack, (ItemModelResolver) (Object) this, displayContext, clientLevel, entity, seed);
                ci.cancel();
                return;
            }

            RpfItemModel rpfItemModel = (RpfItemModel) model;
            if (model != null) {
                if (!rpfItemModel.rpf$testForDelegate(renderState, stack, (ItemModelResolver) (Object) this, displayContext, clientLevel, entity, seed, resourceLocation) || i == packsCont - 1) {
                    ClientItem.Properties properties = rpfModelManager.rpf$getItemPropertiesMaps().get(i).get(resourceLocation);
                    if (properties == null) {
                        properties = ClientItem.Properties.DEFAULT;
                    }
                    renderState.setOversizedInGui(properties.oversizedInGui());
                    this.componentsToProperties.put(stack.getComponents(), properties);
                    renderState.appendModelIdentityElement(new RpfModelIdentity(resourceLocation, i, true)); // for correct GUI rendering
                    model.update(renderState, stack, (ItemModelResolver) (Object) this, displayContext, clientLevel, entity, seed);
                    ci.cancel();
                    return;
                }
            }
        }

        renderState.appendModelIdentityElement(new RpfModelIdentity(resourceLocation, -1, false)); // no model found
        rpfModelManager.rpf$getMissingModel().update(renderState, stack, (ItemModelResolver) (Object) this, displayContext, clientLevel, entity, seed);
        ci.cancel();
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
