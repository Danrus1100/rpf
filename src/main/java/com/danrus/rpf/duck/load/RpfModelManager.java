package com.danrus.rpf.duck.load;

import com.danrus.rpf.core.SignedItemModel;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public interface RpfModelManager {
//    List<Map<ResourceLocation, ItemModel>> rpf$getModelMaps();
    List<Map<ResourceLocation, SignedItemModel>> rpf$getSignedModels();
    List<Map<ResourceLocation, ClientItem.Properties>> rpf$getItemPropertiesMaps();
    ItemModel rpf$getMissingModel();
}
