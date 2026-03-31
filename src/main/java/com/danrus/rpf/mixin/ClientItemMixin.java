package com.danrus.rpf.mixin;

import com.danrus.rpf.duck.RpfClientItem;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.server.packs.PackLocationInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientItem.class)
public class ClientItemMixin implements RpfClientItem {

    @Unique
    private PackLocationInfo rpf$packName;

    @Override
    public void rpf$setPackLocationInfo(PackLocationInfo name) {
        rpf$packName = name;
    }

    @Override
    public PackLocationInfo rpf$getPackLocationInfo() {
        return rpf$packName;
    }
}
