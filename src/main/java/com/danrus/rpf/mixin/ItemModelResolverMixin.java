package com.danrus.rpf.mixin;

import com.danrus.rpf.compat.mtd.MtdBridge;
import com.danrus.rpf.core.item.ModelUpdateContext;
import com.danrus.rpf.core.item.RpfResolversManager;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
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
            ResourceLocation resourceLocation = (ResourceLocation)MtdBridge.swapper.changeModel(stack, () -> stack.get(DataComponents.ITEM_MODEL));
            if (resourceLocation == null) {
                return;
            }

            ClientLevel clientLevel = level instanceof ClientLevel cl ? cl : null;

            ModelUpdateContext context = new ModelUpdateContext(resourceLocation, renderState, displayContext, clientLevel, (ItemModelResolver) (Object) this, seed);
            RpfResolversManager.getInstance().resolve(context, stack, entity, original);
        }

    }

    @WrapMethod(
            method = "shouldPlaySwapAnimation"
    )
    private boolean rpf$shouldPlaySwapAnimation(ItemStack stack, Operation<Boolean> original) {
        return RpfResolversManager.getInstance().shouldPlayAnimationOnSwap(stack, original);
    }
}
