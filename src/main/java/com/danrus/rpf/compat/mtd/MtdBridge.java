//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.danrus.rpf.compat.mtd;

import java.util.function.Supplier;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class MtdBridge {
    public static ItemModelSwapper swapper = (stack, supplier) -> supplier.get();
    public static EntityCapturer capturer = (i, e, s) -> {
    };

    @FunctionalInterface
    public interface EntityCapturer {
        void captureEntity(ItemStack var1, @Nullable LivingEntity var2, ItemStackRenderState var3);
    }

    @FunctionalInterface
    public interface ItemModelSwapper {
        Object changeModel(ItemStack var1, Supplier<Object> var2);
    }
}
