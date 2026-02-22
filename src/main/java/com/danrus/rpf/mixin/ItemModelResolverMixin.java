package com.danrus.rpf.mixin;

import com.danrus.rpf.core.item.RpfResolversManager;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.Zone;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemModelResolver.class)
public class ItemModelResolverMixin<T, R> {
    @WrapMethod(
            method = "appendItemLayers"
    )
    private void rpf$selectModel(ItemStackRenderState renderState, ItemStack stack, ItemDisplayContext displayContext, Level level, LivingEntity entity, int seed, Operation<Void> original) {
        try (Zone zone = Profiler.get().zone("[RPF] resolving")) {
            RpfResolversManager.getInstance().resolve(
                    renderState,
                    stack,
                    displayContext,
                    level,
                    entity,
                    seed,
                    original,
                    (ItemModelResolver) (Object) this
            );
        }

    }

    @WrapMethod(
            method = "shouldPlaySwapAnimation"
    )
    private boolean rpf$shouldPlaySwapAnimation(ItemStack stack, Operation<Boolean> original) {
        return RpfResolversManager.getInstance().shouldPlayAnimationOnSwap(stack, original);
    }
}
