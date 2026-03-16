package com.danrus.rpf.core.item;

import net.minecraft.resources.ResourceLocation;

public record RpfModelIdentity(
        ResourceLocation location,
        String packName) {
}
