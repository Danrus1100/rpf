package com.danrus.rpf.api.event.type;

import com.danrus.rpf.api.event.RpfEvent;
import com.danrus.rpf.core.SignedItemModel;
import net.minecraft.client.renderer.item.ClientItem;

public class PostBakeEvent extends RpfEvent {
    private final ClientItem clientItem;
    private final SignedItemModel result;

    public PostBakeEvent(ClientItem clientItem, SignedItemModel result) {
        this.clientItem = clientItem;
        this.result = result;
    }

    public ClientItem getClientItem() {
        return clientItem;
    }

    public SignedItemModel getResult() {
        return result;
    }
}
