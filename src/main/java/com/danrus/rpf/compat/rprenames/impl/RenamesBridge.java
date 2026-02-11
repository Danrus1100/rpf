package com.danrus.rpf.compat.rprenames.impl;

import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class RenamesBridge {
    public static boolean active = false;

    public static Consumer<List<Map<ResourceLocation, ClientItem>>> itemSetter = null;
    public static BiConsumer<ResourceManager, ProfilerFiller> parser = null;

    public static void update() {
        RpRenamesCompat.update();
    }
}

