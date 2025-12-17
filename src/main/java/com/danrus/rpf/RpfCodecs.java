package com.danrus.rpf;

import com.danrus.rpf.api.DelegateItemModel;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.client.renderer.item.SelectItemModel;

public class RpfCodecs {
    private RpfCodecs() {}

    public static final MapCodec<SelectItemModel.Unbaked> MAP_CODEC_SELECT = RecordCodecBuilder.mapCodec(
            (instance) -> instance.group(
                    SelectItemModel.UnbakedSwitch.MAP_CODEC.forGetter(SelectItemModel.Unbaked::unbakedSwitch),
                    ItemModels.CODEC.optionalFieldOf("fallback").forGetter(SelectItemModel.Unbaked::fallback),
                    Codec.BOOL.optionalFieldOf("delegate", true).forGetter((model) -> {
                        return DelegateItemModel.Unbaked.class.cast(model).rpf$delegate();
                    })
            ).apply(instance, ((unbakedSwitch, fallback, delegate) -> {
                SelectItemModel.Unbaked model = new SelectItemModel.Unbaked(unbakedSwitch, fallback);
                DelegateItemModel.Unbaked.class.cast(model).rpf$setDeligation(delegate);
                return model;
            })));
}
