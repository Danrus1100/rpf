package com.danrus.rpf.mixin.items.select;

import com.danrus.rpf.api.DelegateItemModel;
import com.danrus.rpf.api.RpfItemModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

@Mixin(SelectItemModel.class)
public abstract class SelectItemModelMixin<T> implements DelegateItemModel, RpfItemModel {

    @Unique
    boolean rpf$delegate = true;

    @Override
    public boolean rpf$delegate() {
        return rpf$delegate;
    }

    @Override
    public void rpf$setDeligation(boolean value) {
        this.rpf$delegate = value;
    }

    @Override
    public boolean rpf$testForDelegate(ItemStackRenderState renderState, ItemStack stack, ItemModelResolver itemModelResolver, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed, ResourceLocation itemModelId) {
        if (!this.rpf$delegate) return false;
        if (this.rpf$isFallback()) return true;
        SelectItemModel<T> self = (SelectItemModel<T>) (Object) this;
        T object = self.property.get(stack, level, owner == null ? null : owner.asLivingEntity(), seed, displayContext);
        RpfItemModel itemModel = (RpfItemModel) self.models.get(object, level);
        return itemModel == null || itemModel.rpf$testForDelegate(renderState, stack, itemModelResolver, displayContext, level, owner, seed, itemModelId);
    }
}
