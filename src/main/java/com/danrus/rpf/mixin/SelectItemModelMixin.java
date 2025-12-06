package com.danrus.rpf.mixin;

import com.danrus.rpf.duck.RpfModelManager;
import com.danrus.rpf.duck.RpfSelectItemModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Mixin(SelectItemModel.class)
public class SelectItemModelMixin implements RpfSelectItemModel {

    @Unique
    private Supplier<List<Map<ResourceLocation, ItemModel>>> rpf$modelMapsGetter;

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void rpf$selectItemModelInit(SelectItemModelProperty<?> property, SelectItemModel.ModelSelector<?> models, CallbackInfo ci) {
        RpfModelManager manager = (RpfModelManager) Minecraft.getInstance().getModelManager();
        this.rpf$updateModelMapsGetter(manager::rpf$getModelMaps);
    }

    @Override
    public void rpf$updateModelMapsGetter(Supplier<List<Map<ResourceLocation, ItemModel>>> getter) {
        this.rpf$modelMapsGetter = getter;
    }
}
