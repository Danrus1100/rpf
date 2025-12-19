package com.danrus.rpf.mixin.load;

import com.danrus.rpf.duck.load.RpfBakingResult;
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
    private List<Map<Identifier, ItemModel>> modelsList;

    @Override
    public ModelBakery.BakingResult rpf$setItemModels(List<Map<Identifier, ItemModel>> models) {
        modelsList = models;
        return (ModelBakery.BakingResult) (Object) this;
    }

    @Override
    public List<Map<Identifier, ItemModel>> rpf$geItemModels() {
        return modelsList;
    }
}
