package com.danrus.rpf.core.item;

import com.danrus.rpf.api.RpfItemModelResolver;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RpfResolversManager {
    private static final RpfResolversManager INSTANCE = new RpfResolversManager();

    public static final ResourceLocation DEFAULT_RESOLVER = ResourceLocation.fromNamespaceAndPath("rpf", "v1");
    public static final ResourceLocation VANILLA_RESOLVER = ResourceLocation.withDefaultNamespace("vanilla");
    private static final Logger log = LoggerFactory.getLogger(RpfResolversManager.class);

    private final Map<ResourceLocation, RpfItemModelResolver> resolvers = new HashMap<>();
    private ResourceLocation currentResolver = DEFAULT_RESOLVER;
    @Nullable
    private ResourceLocation pendingResolver;

    public void register(ResourceLocation id, RpfItemModelResolver resolver) {
        resolvers.put(id, resolver);
    }

    public void resolve(
            ItemStackRenderState renderState,
            ItemStack stack,
            ItemDisplayContext displayContext,
            Level level,
            LivingEntity entity,
            int seed,
            Operation<Void> vanilla,
            ItemModelResolver mcResolver
    ) {
        resolvers.get(currentResolver).resolveAndAppendLayer(
                renderState,
                stack,
                displayContext,
                level,
                entity,
                seed,
                vanilla,
                mcResolver
        );
    }

    public boolean shouldPlayAnimationOnSwap(ItemStack stack, Operation<Boolean> vanilla) {
       return resolvers.get(currentResolver).shouldPlaySwapAnimation(stack, vanilla);
    }

    public void setPendingResolver(ResourceLocation pendingResolver) {
        this.pendingResolver = pendingResolver;
    }

    public void applyPendingResolver() {
        if (pendingResolver == null) return;
        currentResolver = pendingResolver;
        pendingResolver = null;
    }

    public List<ResourceLocation> getAvailable() {
        return new ArrayList<>(resolvers.keySet());
    }

    private RpfResolversManager() {}
    public static RpfResolversManager getInstance() { return INSTANCE; }

    public ResourceLocation getCurrent() {
        return currentResolver;
    }
}
