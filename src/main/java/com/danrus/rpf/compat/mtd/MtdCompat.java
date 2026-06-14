//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.danrus.rpf.compat.mtd;

import com.danrus.rpf.compat.RpfCompatInitializer;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.extension.ItemStackExtension;
import net.lopymine.mtd.utils.mixin.ItemRenderStateWithStack;
import net.lopymine.mtd.utils.plugin.TotemDollPlugin;
import net.minecraft.client.player.AbstractClientPlayer;

public class MtdCompat implements RpfCompatInitializer {
    public void init() {
        MtdBridge.swapper = (stack, supplier) -> {
            if (!MyTotemDollClient.canProcess(stack)) {
                return supplier.get();
            } else if (TotemDollPlugin.work(stack)) {
                ItemStackExtension.setModdedModel(stack, true);
                return TotemDollPlugin.ID;
            } else {
                ItemStackExtension.setModdedModel(stack, false);
                return supplier.get();
            }
        };
        MtdBridge.capturer = (stack, entity, renderState) -> {
            ItemStackExtension.setPlayerEntity(stack, (AbstractClientPlayer)null);
            if (entity instanceof AbstractClientPlayer player) {
                ItemStackExtension.setPlayerEntity(stack, player);
            }

            if (renderState instanceof ItemRenderStateWithStack itemRenderStateWithStack) {
                itemRenderStateWithStack.myTotemDoll$setStack(stack);
            }
        };
    }
}
