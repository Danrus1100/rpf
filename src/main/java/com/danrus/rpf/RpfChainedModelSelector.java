package com.danrus.rpf;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import org.jetbrains.annotations.Nullable;

public class RpfChainedModelSelector<T> implements SelectItemModel.ModelSelector<T> {

    private final SelectItemModel.ModelSelector<T> first;
    private final SelectItemModel.ModelSelector<?> second;

    public RpfChainedModelSelector(SelectItemModel.ModelSelector<T> first, SelectItemModel.ModelSelector<?> second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public @Nullable ItemModel get(@Nullable T object, @Nullable ClientLevel clientLevel) {
        ItemModel model = first.get(object, clientLevel);

    }
}
