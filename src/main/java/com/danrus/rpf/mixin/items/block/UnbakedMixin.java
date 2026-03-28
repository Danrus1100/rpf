package com.danrus.rpf.mixin.items.block;

import com.danrus.rpf.duck.item.RpfBlockModelWrapper;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.color.item.ItemTintSource;
//? <=1.21.8
import net.minecraft.client.renderer.block.model.BakedQuad;
//~ block_rename
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(BlockModelWrapper.Unbaked.class)
//~ !block_rename
public class UnbakedMixin {

    @Shadow
    @Final
    private ResourceLocation model;

    @WrapMethod(
            method = "bake"
    )
    private ItemModel rpf$wrapCuboidItemModelWrapper(ItemModel.BakingContext context,
                                                     //? >=26.1
                                                     //Matrix4fc transformation,
                                                     Operation<ItemModel> original) {
        ItemModel wrapper = original.call(context
                //? >=26.1
                //, transformation
        );
        ((RpfBlockModelWrapper) wrapper).rpf$setModelLink(model);
        return wrapper;
    }

}
