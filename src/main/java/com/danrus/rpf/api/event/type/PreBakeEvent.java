package com.danrus.rpf.api.event.type;

import com.danrus.rpf.api.event.RpfEvent;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.resources.Identifier;

public class PreBakeEvent extends RpfEvent {
    private final ClientItem clientItem;
    private final Identifier modelId;
    private final ItemModel.BakingContext bakingContext;

    public PreBakeEvent(ClientItem clientItem,  Identifier modelId, ItemModel.BakingContext bakingContext) {
        this.clientItem = clientItem;
        this.modelId = modelId;
        this.bakingContext = bakingContext;
    }

    public ClientItem getClientItem() {
        return clientItem;
    }

    public ItemModel.BakingContext getBakingContext() {
        return bakingContext;
    }

    public Identifier getModelLocation() {
        return modelId;
    }
}
