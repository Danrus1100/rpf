package com.danrus.rpf.duck.load;

import com.danrus.rpf.core.item.RpfModelIdentity;
import com.danrus.rpf.core.item.SignedItemModel;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;

public interface RpfBakingResult {
    ModelBakery.BakingResult rpf$setSignedItemModels(List<Map<Identifier, SignedItemModel>> models);
    List<Map<Identifier, SignedItemModel>> rpf$getItemSignedModels();

    RpfBakingResult rpf$addItemPropertiesByIdentity(Map<RpfModelIdentity, ClientItem.Properties> properties);
    Map<RpfModelIdentity, ClientItem.Properties> rpf$getItemPropertiesByIdentity();
}
