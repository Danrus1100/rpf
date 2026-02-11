package com.danrus.rpf.duck.load;

import com.danrus.rpf.core.SignedItemModel;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;

public interface RpfBakingResult {
    ModelBakery.BakingResult rpf$setSignedItemModels(List<Map<Identifier, SignedItemModel>> models);
    List<Map<Identifier, SignedItemModel>> rpf$getItemSignedModels();

    RpfBakingResult rpf$setItemProperties(List<Map<Identifier, ClientItem.Properties>> properties);
    List<Map<Identifier, ClientItem.Properties>> rpf$getItemProperties();
}
