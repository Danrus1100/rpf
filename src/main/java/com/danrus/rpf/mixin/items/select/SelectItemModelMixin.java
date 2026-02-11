package com.danrus.rpf.mixin.items.select;

import com.danrus.rpf.api.DelegateItemModel;
import com.danrus.rpf.api.RpfItemModel;
import com.danrus.rpf.logging.ModelTestsResultCollector;
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
    public boolean rpf$testForDelegate(
            ItemStackRenderState renderState,
            ItemStack stack,
            ItemModelResolver itemModelResolver,
            ItemDisplayContext displayContext,
            @Nullable ClientLevel level,
            @Nullable LivingEntity owner,
            int seed,
            ResourceLocation itemModelId,
            String packName,
            ModelTestsResultCollector collector
    ) {
        if (!this.rpf$delegate) return false;
//        if (this.rpf$isFallback()) return true;
        SelectItemModel<T> self = (SelectItemModel<T>) (Object) this;
        T object = self.property.get(stack, level, owner == null ? null : owner
                //? if >=1.21.10
                //.asLivingEntity()
                , seed, displayContext);
        ItemModel itemModel = self.models.get(object, level);

        if (!(itemModel instanceof RpfItemModel)) {
            return itemModel == null || this.rpf$getDelegation();
        }
        if (itemModel != null && itemModel instanceof RpfItemModel rpfItemModel) {
            String propertyValue = object != null ? object.toString() : "null";
            collector.touchNext(this.getClass().getSimpleName() + " proprety: " + propertyValue, packName, itemModelId);
            return rpfItemModel.rpf$testForDelegate(renderState, stack, itemModelResolver, displayContext, level, owner, seed, itemModelId, packName, collector);
        } else {
            collector.touchDelegate(this.getClass().getSimpleName() + " proprety: " + object.toString(), packName, itemModelId);
            return false;
        }
    }
}
