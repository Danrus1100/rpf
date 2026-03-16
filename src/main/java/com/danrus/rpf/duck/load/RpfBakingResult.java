package com.danrus.rpf.duck.load;

import com.danrus.rpf.core.item.RpfModelIdentity;
import com.danrus.rpf.core.item.SignedItemModel;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public interface RpfBakingResult {
    ModelBakery.BakingResult rpf$setSignedItemModels(List<Map<ResourceLocation, SignedItemModel>> models);
    List<Map<ResourceLocation, SignedItemModel>> rpf$getItemSignedModels();

    RpfBakingResult rpf$setItemPropertiesById(List<Map<ResourceLocation, ClientItem.Properties>> properties);
    List<Map<ResourceLocation, ClientItem.Properties>> rpf$getItemPropertiesById();

    RpfBakingResult rpf$addItemPropertiesByIdentity(Map<RpfModelIdentity, ClientItem.Properties> properties);
    Map<RpfModelIdentity, ClientItem.Properties> rpf$getItemPropertiesByIdentity();
}
