package com.danrus.rpf.mixin.items;

import com.danrus.rpf.api.RpfItemModel;
import net.minecraft.client.renderer.item.*;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({BlockModelWrapper.class,
        BundleSelectedItemSpecialRenderer.class,
        CompositeModel.class,
        ConditionalItemModel.class,
        EmptyModel.class,
        MissingItemModel.class,
        RangeSelectItemModel.class,
        SelectItemModel.class,
        SpecialModelWrapper.class
})
public class ItemModelMixin implements RpfItemModel {
    @Unique private boolean rpf$isFallbackModel = false;

    public void rpf$setFallback(){ this.rpf$isFallbackModel = true; }
    public boolean rpf$isFallback(){ return rpf$isFallbackModel; }
}
