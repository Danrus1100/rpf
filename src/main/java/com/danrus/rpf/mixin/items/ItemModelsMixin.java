package com.danrus.rpf.mixin.items;

import com.danrus.rpf.RpfCodecs;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemModels.class)
public class ItemModelsMixin {
    @WrapOperation(
            method = "bootstrap",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ExtraCodecs$LateBoundIdMapper;put(Ljava/lang/Object;Ljava/lang/Object;)Lnet/minecraft/util/ExtraCodecs$LateBoundIdMapper;", ordinal = 6)
    )
    private static <I, V> ExtraCodecs.LateBoundIdMapper<I, V> rpf$redirectSelectMapCodec(ExtraCodecs.LateBoundIdMapper instance, I id, V value, Operation<ExtraCodecs.LateBoundIdMapper<I, V>> original){
        return original.call(instance, id, RpfCodecs.MAP_CODEC_SELECT);
    }
}
