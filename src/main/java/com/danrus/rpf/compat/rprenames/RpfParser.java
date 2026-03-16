package com.danrus.rpf.compat.rprenames;

import com.hiword9.rprenames.api.core.renames_manager.RenamesManager;
import com.hiword9.rprenames.mod.impl.rename.ItemModelRename;
import com.hiword9.rprenames.mod.impl.renames_manager.updatable.parser.item_model.ItemModelParser;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.*;

public class RpfParser extends ItemModelParser {

    private List<Map<ResourceLocation, ClientItem>> listItemAssets = new ArrayList<>();

    public RpfParser(RenamesManager<? super ItemModelRename> renamesManager) {
        super(renamesManager);
    }

    public void updateClientItem(List<Map<ResourceLocation, ClientItem>> itemAssets) {
        this.listItemAssets = itemAssets;
    }

    public void parse(ResourceManager resourceManager, ProfilerFiller profiler) {
        for (Map<ResourceLocation, ClientItem> itemAssets : listItemAssets) {
            this.updateItemAssets(itemAssets);
            super.parse(resourceManager, profiler);
        }


    }

}
