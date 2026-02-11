package com.danrus.rpf.mixin;

import com.danrus.rpf.duck.RpfClientItem;
import net.minecraft.client.renderer.item.ClientItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientItem.class)
public class ClientItemMixin implements RpfClientItem {

    @Unique
    private String rpf$packName;

    @Override
    public void rpf$setPackName(String name) {
        rpf$packName = name;
    }

    @Override
    public String rpf$getPackName() {
        return rpf$packName;
    }
}
