package com.danrus.rpf.duck.load;

import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;

public interface RpfModelBakery {
    ModelBakery rpf$setClientItems(List<Map<Identifier, ClientItem>> items);
}
