package com.danrus.rpf.mixin.items.composite;

import com.danrus.rpf.api.RpfItemModel;
import com.danrus.rpf.logging.ModelTestsResultCollector;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.CompositeModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(CompositeModel.class)
public abstract class CompositeModelMixin implements RpfItemModel {

    @Shadow
    @Final
    private List<ItemModel> models;

    @Override
    public boolean rpf$doDelegate(ItemStackRenderState renderState, ItemStack stack, ItemModelResolver itemModelResolver, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable LivingEntity owner, @Nullable ItemModel prev, int seed, ResourceLocation itemModelId, String packName, ModelTestsResultCollector collector) {
        if (rpf$isFallback()) {
            models.forEach(model ->  ((RpfItemModel) model).rpf$markAsFallback());
        }
        boolean delegate = false;
        for (ItemModel model : models) {
            if (((RpfItemModel)model).rpf$doDelegate(renderState, stack, itemModelResolver, displayContext, level, owner, prev, seed, itemModelId, packName, collector)) {
                delegate = true;
            }
        }

        StringBuilder modesString = new StringBuilder();
        models.forEach(m -> {
            modesString.append(m.getClass().getSimpleName() + ", ");
        });

        if (delegate) {
            collector.touchDelegate(this.getClass().getSimpleName() + " models: " + models.size() + ": " + modesString, packName, itemModelId);
        } else {
            collector.touchAllow(this.getClass().getSimpleName() + " models: " + models.size() + ": " + modesString, packName, itemModelId);
        }
        return delegate;
    }
}
