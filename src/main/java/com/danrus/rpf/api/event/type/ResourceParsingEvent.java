package com.danrus.rpf.api.event.type;

import com.danrus.rpf.api.event.AbstractStagedEvent;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.jetbrains.annotations.Nullable;

public class ResourceParsingEvent extends AbstractStagedEvent {
    private final ResourceLocation id;
    private final Resource resource;
    private final RegistryAccess.Frozen registryAccess;
    @Nullable
    private final ClientItem clientItem;

    public ResourceParsingEvent(Stage stage, ResourceLocation id, Resource resource, RegistryAccess.Frozen registryAccess, @Nullable ClientItem clientItem) {
        super(stage);
        this.id = id;
        this.resource = resource;
        this.registryAccess = registryAccess;
        this.clientItem = clientItem;
    }

    public ResourceLocation getLocation() {
        return id;
    }

    public Resource getResource() {
        return resource;
    }

    public RegistryAccess.Frozen getRegistryAccess() {
        return registryAccess;
    }

    public @Nullable ClientItem getClientItem() {
        return clientItem;
    }
}
