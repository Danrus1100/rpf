package com.danrus.rpf.mixin.items.select;

import com.danrus.rpf.api.DelegateItemModel;
import com.danrus.rpf.duck.item.RpfSelectModelUnbakedSwitch;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.util.RegistryContextSwapper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SelectItemModel.UnbakedSwitch.class)
public abstract class UnbakedSwitchMixin <P extends SelectItemModelProperty<T>, T> implements RpfSelectModelUnbakedSwitch {

    @Shadow
    @Final
    private P property;

    @Unique
    private boolean delegation;

    @Shadow
    protected abstract SelectItemModel.ModelSelector<T> createModelGetter(Object2ObjectMap<T, ItemModel> models, @Nullable RegistryContextSwapper contextSwapper);

    @Inject(
            method = "bake",
            at = @At("RETURN"),
            cancellable = true
    )
    private void rpf$selectInit(ItemModel.BakingContext bakingContext, ItemModel model, CallbackInfoReturnable<ItemModel> cir, @Local Object2ObjectMap<T, ItemModel> object2ObjectMap) {
        SelectItemModel<T> selectItemModel = new SelectItemModel<>(this.property, this.createModelGetter(object2ObjectMap, bakingContext.contextSwapper()));
        ((DelegateItemModel) selectItemModel).rpf$setDeligation(this.delegation);
        cir.setReturnValue(selectItemModel);
    }

    @Override
    public void rpf$setUnbakedDelegation(boolean value) {
        this.delegation = value;
    }
}
