package com.danrus.rpf;

import net.minecraft.resources.Identifier;

public record RpfModelIdentity(
        Identifier location,
        int selectedPack,
        boolean delegate
) {
}
