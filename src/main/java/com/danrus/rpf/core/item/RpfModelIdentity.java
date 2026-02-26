package com.danrus.rpf.core.item;

import net.minecraft.resources.Identifier;

public record RpfModelIdentity(
        Identifier location,
        String packName) {
}
