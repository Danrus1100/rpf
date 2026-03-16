package com.danrus.rpf.duck.load;

import com.danrus.rpf.core.item.RpfModelIdentity;
import com.danrus.rpf.core.item.SignedItemModel;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public interface RpfModelManager {
//    List<Map<ResourceLocation, ItemModel>> rpf$getModelMaps();
    List<Map<ResourceLocation, SignedItemModel>> rpf$getSignedModels();
    List<Map<ResourceLocation, ClientItem.Properties>> rpf$getItemPropertiesMaps();
    ClientItem.Properties rpf$getProperties(RpfModelIdentity identity);
    SignedItemModel rpf$getVanillaModel(ResourceLocation location);
    ItemModel rpf$getMissingModel();
}
