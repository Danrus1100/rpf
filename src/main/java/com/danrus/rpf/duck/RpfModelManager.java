package com.danrus.rpf.duck;

import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface RpfModelManager {
    List<Map<ResourceLocation, ItemModel>> rpf$getModelMaps();
}
