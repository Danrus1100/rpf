package com.danrus.rpf.mixin.load;

import com.danrus.rpf.core.item.RpfModelIdentity;
import com.danrus.rpf.core.item.SignedItemModel;
import com.danrus.rpf.duck.load.RpfBakingResult;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.Map;

@Mixin(ModelBakery.BakingResult.class)
public class BakingResultMixin implements RpfBakingResult {

    @Unique
    private List<Map<ResourceLocation, SignedItemModel>> modelsList;

    @Unique
    private List<Map<ResourceLocation, ClientItem.Properties>> propertiesList;

    @Unique
    private Map<RpfModelIdentity, ClientItem.Properties> propertiesByIdentity;

    @Override
    public ModelBakery.BakingResult rpf$setSignedItemModels(List<Map<ResourceLocation, SignedItemModel>> models) {
        this.modelsList = models;
        return (ModelBakery.BakingResult) (Object) this;
    }

    @Override
    public List<Map<ResourceLocation, SignedItemModel>> rpf$getItemSignedModels() {
        return modelsList;
    }

    @Override
    public RpfBakingResult rpf$setItemPropertiesById(List<Map<ResourceLocation, ClientItem.Properties>> properties) {
        this.propertiesList = properties;
        return this;
    }

    @Override
    public List<Map<ResourceLocation, ClientItem.Properties>> rpf$getItemPropertiesById() {
        return propertiesList;
    }

    @Override
    public RpfBakingResult rpf$addItemPropertiesByIdentity(Map<RpfModelIdentity, ClientItem.Properties> properties) {
        this.propertiesByIdentity = properties;
        return this;
    }

    @Override
    public Map<RpfModelIdentity, ClientItem.Properties> rpf$getItemPropertiesByIdentity() {
        return propertiesByIdentity;
    }
}
