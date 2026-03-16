package com.danrus.rpf.api.event.type;

import com.danrus.rpf.api.event.AbstractStagedEvent;
import com.danrus.rpf.core.load.RpfClientItemInfoLoader;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.client.resources.model.ModelDiscovery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public class ModelDiscoveryEvent extends AbstractStagedEvent {
    private final ModelDiscovery modelDiscovery;
    private final Map<ResourceLocation, UnbakedModel> blockModels;
    private final BlockStateModelLoader.LoadedModels loadedModels;
    private final List<RpfClientItemInfoLoader.LoadedClientInfos> itemLayers;

    public ModelDiscoveryEvent(Stage stage, ModelDiscovery modelDiscovery, Map<ResourceLocation, UnbakedModel> blockModels, BlockStateModelLoader.LoadedModels loadedModels, List<RpfClientItemInfoLoader.LoadedClientInfos> itemLayers) {
        super(stage);
        this.modelDiscovery = modelDiscovery;
        this.blockModels = blockModels;
        this.loadedModels = loadedModels;
        this.itemLayers = itemLayers;
    }

    public ModelDiscovery getModelDiscovery() {
        return modelDiscovery;
    }

    public Map<ResourceLocation, UnbakedModel> getBlockModels() {
        return blockModels;
    }

    public BlockStateModelLoader.LoadedModels getLoadedModels() {
        return loadedModels;
    }

    public List<RpfClientItemInfoLoader.LoadedClientInfos> getItemLayers() {
        return itemLayers;
    }
}
