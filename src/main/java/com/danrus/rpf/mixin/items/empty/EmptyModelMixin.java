package com.danrus.rpf.mixin.items.empty;

import com.danrus.rpf.api.RpfItemModel;
import com.danrus.rpf.api.TestsResultCollector;
import com.danrus.rpf.core.item.ModelUpdateContext;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.EmptyModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EmptyModel.class)
public abstract class EmptyModelMixin implements RpfItemModel {

    @Override
    public boolean rpf$doDelegate(ModelUpdateContext context, ItemStack stack, @Nullable LivingEntity owner, @Nullable ItemModel prev, TestsResultCollector collector) {
        return false;
    }
}
