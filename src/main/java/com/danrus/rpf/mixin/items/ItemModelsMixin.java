package com.danrus.rpf.mixin.items;

import com.danrus.rpf.api.codec.RpfModelsCodecsExtends;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemModels.class)
public class ItemModelsMixin {
    static {
        try {
            Class.forName("com.danrus.rpf.RpfCodecs");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load RpfCodecs", e);
        }
    }

    @SuppressWarnings("unchecked")
    @WrapOperation(
            method = "bootstrap",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ExtraCodecs$LateBoundIdMapper;put(Ljava/lang/Object;Ljava/lang/Object;)Lnet/minecraft/util/ExtraCodecs$LateBoundIdMapper;")
    )
    private static <I, V> ExtraCodecs.LateBoundIdMapper<I, V> rpf$redirectMapCodec(ExtraCodecs.LateBoundIdMapper instance, I id, V value, Operation<ExtraCodecs.LateBoundIdMapper<I, V>> original) {
        V valueToPut = value;
        if (id instanceof Identifier location && value instanceof MapCodec<?> mapCodec) {
            valueToPut = (V) RpfModelsCodecsExtends.getInstance().wrap(location, (MapCodec<Object>) mapCodec);
        }
        return original.call(instance, id, valueToPut);
    }
}
