package com.danrus.rpf.mixin.items.block;

import com.danrus.rpf.api.DelegateItemModel;
import com.danrus.rpf.duck.item.RpfBlockModelWrapper;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Function;

@Mixin(BlockModelWrapper.Unbaked.class)
public class UnbakedMixin {

    @Shadow
    @Final
    private List<ItemTintSource> tints;

    @Shadow
    @Final
    private ResourceLocation model;

    //? if <=1.21.10 {
    @Inject(
            method = "bake",
            at = @At("RETURN"),
            cancellable = true
    )
    private void rpf$selectInit(ItemModel.BakingContext context, CallbackInfoReturnable<ItemModel> cir, @Local List<BakedQuad> list, @Local ModelRenderProperties modelRenderProperties) {
        BlockModelWrapper modelWrapper = new BlockModelWrapper(this.tints, list, modelRenderProperties);
        ((RpfBlockModelWrapper) modelWrapper).rpf$setModelLink(this.model);
        cir.setReturnValue(modelWrapper);
    }
    //?} else {
    /*@WrapOperation(
            method = "bake",
            at = @At(value = "NEW", target = "(Ljava/util/List;Ljava/util/List;Lnet/minecraft/client/renderer/item/ModelRenderProperties;Ljava/util/function/Function;)Lnet/minecraft/client/renderer/item/BlockModelWrapper;")
    )
    private BlockModelWrapper rpf$wrapBlockModelWrapper(List list, List list2, ModelRenderProperties modelRenderProperties, Function function, Operation<BlockModelWrapper> original) {
        BlockModelWrapper wrapper = original.call(list, list2, modelRenderProperties, function);
        ((RpfBlockModelWrapper) wrapper).rpf$setModelLink(this.model);
        return wrapper;
    }
    *///?}

}
