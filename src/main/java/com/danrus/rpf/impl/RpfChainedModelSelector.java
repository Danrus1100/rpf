package com.danrus.rpf.impl;

import com.danrus.rpf.duck.RpfItemModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import org.jetbrains.annotations.Nullable;

public class RpfChainedModelSelector implements SelectItemModel.ModelSelector<Object> {

    private final SelectItemModel.ModelSelector<@Nullable Object> first;
    private final SelectItemModel.ModelSelector<@Nullable Object> second;

    public RpfChainedModelSelector(SelectItemModel.ModelSelector<@Nullable Object> first, SelectItemModel.ModelSelector<@Nullable Object> second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public @Nullable ItemModel get(@Nullable Object object, @Nullable ClientLevel clientLevel) {
        ItemModel model = first.get(object, clientLevel);
        RpfItemModel rpfModel = (RpfItemModel) model;
        if (rpfModel.rpf$isFallback() && rpfModel.rpf$delegate()) {
            return second.get(object, clientLevel);
        } else {
            return model;
        }
    }
}
