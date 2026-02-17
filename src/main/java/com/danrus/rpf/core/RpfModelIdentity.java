package com.danrus.rpf.core;

import net.minecraft.resources.ResourceLocation;

public record RpfModelIdentity(
        ResourceLocation location,
        String packName) {
}
