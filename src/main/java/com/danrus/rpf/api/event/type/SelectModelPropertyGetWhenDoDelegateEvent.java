package com.danrus.rpf.api.event.type;

import com.danrus.rpf.api.event.RpfEvent;
import com.danrus.rpf.core.item.ModelUpdateContext;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class SelectModelPropertyGetWhenDoDelegateEvent<T> extends RpfEvent {
    private final SelectItemModel<T> model;
    private final ModelUpdateContext context;
    private final ItemStack stack;
    @Nullable
    private final LivingEntity owner;
    private final SelectItemModelProperty<T> property;
    private Supplier<T> getter;

    public SelectModelPropertyGetWhenDoDelegateEvent(ModelUpdateContext context, ItemStack stack, @Nullable LivingEntity owner, SelectItemModelProperty<T> property, SelectItemModel<T> model, Supplier<T> getter) {
        this.context = context;
        this.stack = stack;
        this.owner = owner;
        this.property = property;
        this.model = model;
        this.getter = getter;
    }

    public ModelUpdateContext getContext() {
        return context;
    }

    public ItemStack getStack() {
        return stack;
    }

    public @Nullable LivingEntity getOwner() {
        return owner;
    }

    public SelectItemModelProperty<T> getProperty() {
        return property;
    }

    public SelectItemModel<T> getModel() {
        return model;
    }

    public T getObject() {
        return getter.get();
    }

    public void setGetter(Supplier<T> getter) {
        this.getter = getter;
    }
}
