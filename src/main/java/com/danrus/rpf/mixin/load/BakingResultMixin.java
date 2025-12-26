package com.danrus.rpf.mixin.load;

import com.danrus.rpf.duck.load.RpfBakingResult;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.Map;

@Mixin(ModelBakery.BakingResult.class)
public class BakingResultMixin implements RpfBakingResult {

    @Unique
    private List<Map<ResourceLocation, ItemModel>> modelsList;

    @Unique
    private List<Map<ResourceLocation, ClientItem.Properties>> propertiesList;

    @Override
    public ModelBakery.BakingResult rpf$setItemModels(List<Map<ResourceLocation, ItemModel>> models) {
        modelsList = models;
        return (ModelBakery.BakingResult) (Object) this;
    }

    @Override
    public List<Map<ResourceLocation, ItemModel>> rpf$geItemModels() {
        return modelsList;
    }

    @Override
    public RpfBakingResult rpf$setItemProperties(List<Map<ResourceLocation, ClientItem.Properties>> properties) {
        this.propertiesList = properties;
        return this;
    }

    @Override
    public List<Map<ResourceLocation, ClientItem.Properties>> rpf$getItemProperties() {
        return propertiesList;
    }
}
