package com.danrus.rpf.duck.load;

import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public interface RpfBakingResult {
    ModelBakery.BakingResult rpf$setItemModels(List<Map<ResourceLocation, ItemModel>> models);
    List<Map<ResourceLocation, ItemModel>> rpf$geItemModels();
}
