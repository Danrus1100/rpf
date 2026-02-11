package com.danrus.rpf.duck.item;

import net.minecraft.resources.Identifier;

public interface RpfBlockModelWrapper {
    void rpf$setModelLink(Identifier location);
    Identifier rpf$getModelLink();
}
