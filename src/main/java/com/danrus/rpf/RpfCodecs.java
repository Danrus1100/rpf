package com.danrus.rpf;

import com.danrus.rpf.api.DelegateItemModel;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;

public class RpfCodecs {
    private RpfCodecs() {}

    public static final MapCodec<SelectItemModel.Unbaked> MAP_CODEC_SELECT = RecordCodecBuilder.mapCodec(
            (instance) -> instance.group(
                    SelectItemModel.UnbakedSwitch.MAP_CODEC.forGetter(SelectItemModel.Unbaked::unbakedSwitch),
                    ItemModels.CODEC.optionalFieldOf("fallback").forGetter(SelectItemModel.Unbaked::fallback),
                    Codec.BOOL.optionalFieldOf("delegate", true).forGetter((model) -> {
                        return DelegateItemModel.Unbaked.class.cast(model).rpf$getDelegation();
                    })
            ).apply(instance, ((unbakedSwitch, fallback, delegate) -> {
                SelectItemModel.Unbaked model = new SelectItemModel.Unbaked(unbakedSwitch, fallback);
                DelegateItemModel.Unbaked.class.cast(model).rpf$setDeligation(delegate);
                return model;
            })));

    public static final MapCodec<RangeSelectItemModel.Unbaked> MAP_CODEC_RANGE = RecordCodecBuilder.mapCodec(
            (instance) -> instance.group(
                        RangeSelectItemModelProperties.MAP_CODEC.forGetter(RangeSelectItemModel.Unbaked::property),
                        Codec.FLOAT.optionalFieldOf("scale", 1.0F).forGetter(RangeSelectItemModel.Unbaked::scale),
                        RangeSelectItemModel.Entry.CODEC.listOf().fieldOf("entries").forGetter(RangeSelectItemModel.Unbaked::entries),
                        ItemModels.CODEC.optionalFieldOf("fallback").forGetter(RangeSelectItemModel.Unbaked::fallback),
                        Codec.BOOL.optionalFieldOf("delegate", true).forGetter((model) -> {
                            return DelegateItemModel.Unbaked.class.cast(model).rpf$getDelegation();
                        })
                    ).apply(instance, ((property, scale, entries, fallback, delegate) -> {
                        RangeSelectItemModel.Unbaked model = new RangeSelectItemModel.Unbaked(property, scale, entries, fallback);
                        DelegateItemModel.Unbaked.class.cast(model).rpf$setDeligation(delegate);
                        return model;
                    })));
}
