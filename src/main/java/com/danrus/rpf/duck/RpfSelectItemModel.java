package com.danrus.rpf.duck;

import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface RpfSelectItemModel {
    void rpf$updateModelMapsGetter(Supplier<List<Map<ResourceLocation, ItemModel>>> getter);
}
