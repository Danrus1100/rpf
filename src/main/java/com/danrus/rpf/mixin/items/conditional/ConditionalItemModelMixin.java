package com.danrus.rpf.mixin.items.conditional;

import com.danrus.rpf.api.RpfItemModel;
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
    public boolean rpf$testForDelegate(ItemStackRenderState renderState, ItemStack stack, ItemModelResolver itemModelResolver, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed, ResourceLocation itemModelId) {
        ItemModel model = (property.get(
                stack,
                level,
                owner == null ? null : owner
                //? if >=1.21.10
                //.asLivingEntity()
                ,
                seed,
                displayContext
        ) ? onTrue : onFalse);

        if (model instanceof RpfItemModel rpfItemModel) {
            return rpfItemModel.rpf$testForDelegate(renderState, stack, itemModelResolver, displayContext, level, owner, seed, itemModelId);
        }
        return RpfItemModel.super.rpf$testForDelegate(renderState, stack, itemModelResolver, displayContext, level, owner, seed, itemModelId);
    }
}
