package com.danrus.rpf.mixin.items.common;

import com.danrus.rpf.api.codec.RpfModelsCodecsExtends;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin({BlockModelWrapper.Unbaked.class,
        BundleSelectedItemSpecialRenderer.Unbaked.class,
        CompositeModel.Unbaked.class,
        ConditionalItemModel.Unbaked.class,
        EmptyModel.Unbaked.class,
        RangeSelectItemModel.Unbaked.class,
        SelectItemModel.Unbaked.class,
        SpecialModelWrapper.Unbaked.class
})
public abstract class ItemModelUnbakedMixin {
    @SuppressWarnings("unchecked")
    @WrapMethod(method = "type")
    MapCodec<?> rpf$wrapType(Operation<MapCodec<?>> original) {
        return RpfModelsCodecsExtends.getInstance().getWrapped(original.call());
    }
}

