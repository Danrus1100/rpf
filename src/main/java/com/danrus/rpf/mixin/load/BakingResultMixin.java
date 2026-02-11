package com.danrus.rpf.mixin.load;

import com.danrus.rpf.core.SignedItemModel;
import com.danrus.rpf.duck.load.RpfBakingResult;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.Map;

@Mixin(ModelBakery.BakingResult.class)
public class BakingResultMixin implements RpfBakingResult {

    @Unique
    private List<Map<Identifier, SignedItemModel>> modelsList;

    @Unique
    private List<Map<Identifier, ClientItem.Properties>> propertiesList;

    @Override
    public ModelBakery.BakingResult rpf$setSignedItemModels(List<Map<Identifier, SignedItemModel>> models) {
        this.modelsList = models;
        return (ModelBakery.BakingResult) (Object) this;
    }

    @Override
    public List<Map<Identifier, SignedItemModel>> rpf$getItemSignedModels() {
        return modelsList;
    }

    @Override
    public RpfBakingResult rpf$setItemProperties(List<Map<Identifier, ClientItem.Properties>> properties) {
        this.propertiesList = properties;
        return this;
    }

    @Override
    public List<Map<Identifier, ClientItem.Properties>> rpf$getItemProperties() {
        return propertiesList;
    }
}
