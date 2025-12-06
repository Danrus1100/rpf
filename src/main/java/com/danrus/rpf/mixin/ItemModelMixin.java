package com.danrus.rpf.mixin;

import com.danrus.rpf.duck.RpfItemModel;
import net.minecraft.client.renderer.item.*;
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
    @Unique private boolean rpf$doDelegate = true;

    public void rpf$setFallback(){this.rpf$isFallbackModel = true;}
    public boolean rpf$isFallback(){return rpf$isFallbackModel;}
    public void rpf$setDeligation(boolean value) {rpf$doDelegate = value;}
    public boolean rpf$delegate() {return rpf$doDelegate;}
}
