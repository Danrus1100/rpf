package com.danrus.rpf.duck;

import net.minecraft.server.packs.PackLocationInfo;

public interface RpfClientItem {
    void rpf$setPackLocationInfo(PackLocationInfo name);
    PackLocationInfo rpf$getPackLocationInfo();

    @Deprecated
    default String rpf$getPackName() {
        return rpf$getPackLocationInfo().id();
    }
}
