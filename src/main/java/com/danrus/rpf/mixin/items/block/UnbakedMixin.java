package com.danrus.rpf.mixin.items.block;

import com.danrus.rpf.duck.item.RpfBlockModelWrapper;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CuboidItemModelWrapper.Unbaked.class)
public class UnbakedMixin {

    @Shadow
    @Final
    private Identifier model;

    @WrapMethod(
            method = "bake"
    )
    private ItemModel rpf$wrapCuboidItemModelWrapper(ItemModel.BakingContext context,
                                                     Matrix4fc transformation,
                                                     Operation<ItemModel> original) {
        ItemModel wrapper = original.call(context, transformation);
        ((RpfBlockModelWrapper) wrapper).rpf$setModelLink(model);
        return wrapper;
    }

}
