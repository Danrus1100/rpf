package com.danrus.rpf.duck.load;

import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public interface RpfModelBakery {
    ModelBakery rpf$setClientItems(List<Map<ResourceLocation, ClientItem>> items);
}
