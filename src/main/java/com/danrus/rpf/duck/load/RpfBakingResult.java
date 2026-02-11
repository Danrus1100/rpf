package com.danrus.rpf.duck.load;

import com.danrus.rpf.core.SignedItemModel;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public interface RpfBakingResult {
    ModelBakery.BakingResult rpf$setSignedItemModels(List<Map<ResourceLocation, SignedItemModel>> models);
    List<Map<ResourceLocation, SignedItemModel>> rpf$getItemSignedModels();

    RpfBakingResult rpf$setItemProperties(List<Map<ResourceLocation, ClientItem.Properties>> properties);
    List<Map<ResourceLocation, ClientItem.Properties>> rpf$getItemProperties();
}
