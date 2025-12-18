package com.danrus.rpf;

import net.minecraft.resources.ResourceLocation;

public record RpfModelIdentity(
        ResourceLocation location,
        int selectedPack,
        boolean delegate
) {
}
