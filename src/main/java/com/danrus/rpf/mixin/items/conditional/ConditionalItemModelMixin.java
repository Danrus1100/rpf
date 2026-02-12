package com.danrus.rpf.mixin.items.conditional;

import com.danrus.rpf.api.RpfItemModel;
import com.danrus.rpf.logging.ModelTestsResultCollector;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ConditionalItemModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.properties.conditional.ItemModelPropertyTest;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
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
    public boolean rpf$doDelegate(ItemStackRenderState renderState, ItemStack stack, ItemModelResolver itemModelResolver, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable LivingEntity owner, @Nullable ItemModel prev, int seed, ResourceLocation itemModelId, String packName, ModelTestsResultCollector collector) {
        boolean isTrue = property.get(
                stack,
                level,
                owner == null ? null : owner
                //? if >=1.21.10
                //.asLivingEntity()
                ,
                seed,
                displayContext
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
            collector.touchNext(this.getClass().getSimpleName() + " (" + isTrue + ")", packName, itemModelId);
            return rpfItemModel.rpf$doDelegate(renderState, stack, itemModelResolver, displayContext, level, owner, (ItemModel) (Object) this, seed, itemModelId, packName, collector);
        }
        return this.rpf$isFallback();
    }
}
