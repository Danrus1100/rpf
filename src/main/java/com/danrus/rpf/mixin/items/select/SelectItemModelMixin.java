package com.danrus.rpf.mixin.items.select;

import com.danrus.rpf.api.DelegateItemModel;
import com.danrus.rpf.api.RpfItemModel;
import com.danrus.rpf.api.TestsResultCollector;
import com.danrus.rpf.core.item.ModelUpdateContext;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.resources.ResourceLocation;
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
    public boolean rpf$getDelegation() {
        return rpf$delegate;
    }

    @Override
    public void rpf$setDeligation(boolean value) {
        this.rpf$delegate = value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean rpf$doDelegate(ModelUpdateContext context, ItemStack stack, @Nullable LivingEntity owner, @Nullable ItemModel prev, TestsResultCollector collector) {
        if (!this.rpf$delegate) {
            collector.hit(this.getClass(), " force cancel delegate");
            return false;
        }
//        if (this.rpf$isFallback()) return true;
        SelectItemModel<T> self = (SelectItemModel<T>) (Object) this;
        T object = self.property.get(stack, context.level(), owner == null ? null : owner
                //? if >=1.21.10
                //.asLivingEntity()
                , context.seed(), context.displayContext());
        ItemModel itemModel = self.models.get(object, context.level());

        if (itemModel instanceof RpfItemModel rpfItemModel) {
            if (prev != null && this.rpf$isFallback()) rpfItemModel.rpf$markAsFallback();
            String propertyValue = object != null ? object.toString() : "null";
            collector.next(this.getClass(), " proprety: " + propertyValue, rpfItemModel.rpf$isFallback());
            return rpfItemModel.rpf$doDelegate(context, stack, owner, (ItemModel) (Object) this, collector);
        } else {
            collector.delegate(this.getClass(), " proprety: " + object.toString());
            return itemModel == null || this.rpf$getDelegation();
        }
    }
}
