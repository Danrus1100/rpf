package com.danrus.rpf.core.item;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;

public record ModelUpdateContext(
        Identifier location,
        ItemStackRenderState renderState,
        ItemDisplayContext displayContext,
        @Nullable ClientLevel level,
        ItemModelResolver mcResolver,
        int seed
) {}
