package com.danrus.rpf.duck.load;

import com.danrus.rpf.core.item.RpfModelIdentity;
import com.danrus.rpf.core.item.SignedItemModel;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;

public interface RpfModelManager {
//    List<Map<ResourceLocation, ItemModel>> rpf$getModelMaps();
    List<Map<Identifier, SignedItemModel>> rpf$getSignedModels();
    List<Map<Identifier, ClientItem.Properties>> rpf$getItemPropertiesMaps();
    ClientItem.Properties rpf$getProperties(RpfModelIdentity identity);
    SignedItemModel rpf$getVanillaModel(Identifier location);
    ItemModel rpf$getMissingModel();
}
