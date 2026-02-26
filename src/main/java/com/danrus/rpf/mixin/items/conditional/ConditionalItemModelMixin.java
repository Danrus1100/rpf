package com.danrus.rpf.mixin.items.conditional;

import com.danrus.rpf.api.RpfItemModel;
import com.danrus.rpf.api.TestsResultCollector;
import com.danrus.rpf.core.item.ModelUpdateContext;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ConditionalItemModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.properties.conditional.ItemModelPropertyTest;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ConditionalItemModel.class)
public abstract class ConditionalItemModelMixin implements RpfItemModel {

    @Shadow
    @Final
    private ItemModelPropertyTest property;

    @Shadow
    @Final
    private ItemModel onTrue;

    @Shadow
    @Final
    private ItemModel onFalse;

    @Override
    public boolean rpf$doDelegate(ModelUpdateContext context, ItemStack stack, @Nullable ItemOwner owner, @Nullable ItemModel prev, TestsResultCollector collector) {
        boolean isTrue = property.get(
                stack,
                context.level(),
                owner == null ? null : owner
                //? if >=1.21.10
                .asLivingEntity()
                ,
                context.seed(),
                context.displayContext()
        );
        ItemModel model = isTrue ? onTrue : onFalse;
        if (prev != null && this.rpf$isFallback()) {
            ((RpfItemModel) onTrue).rpf$markAsFallback();
            ((RpfItemModel) onFalse).rpf$markAsFallback();
        }
        if (prev == null) {
            ((RpfItemModel) onFalse).rpf$markAsFallback();
        }
        if (model instanceof RpfItemModel rpfItemModel) {
            collector.next(this.getClass(), " (" + isTrue + ")");
            return rpfItemModel.rpf$doDelegate(context, stack, owner, (ItemModel) (Object) this, collector);
        }
        return this.rpf$isFallback();
    }
}
