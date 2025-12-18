package com.danrus.rpf.duck.item;

import net.minecraft.resources.ResourceLocation;

public interface RpfBlockModelWrapper {
    void rpf$setModelLink(ResourceLocation location);
    ResourceLocation rpf$getModelLink();
}
