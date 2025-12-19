package com.danrus.rpf.duck.load;

import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;

public interface RpfBakingResult {
    ModelBakery.BakingResult rpf$setItemModels(List<Map<Identifier, ItemModel>> models);
    List<Map<Identifier, ItemModel>> rpf$geItemModels();
}
