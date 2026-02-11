package com.danrus.rpf.core;

import net.minecraft.resources.Identifier;

public record RpfModelIdentity(
        Identifier location,
        int selectedPack,
        boolean delegate
) {}
