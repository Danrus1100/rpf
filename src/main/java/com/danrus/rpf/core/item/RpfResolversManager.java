package com.danrus.rpf.core.item;

import com.danrus.rpf.api.RpfItemModelResolver;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RpfResolversManager {
    private static final RpfResolversManager INSTANCE = new RpfResolversManager();

    public static final Identifier DEFAULT_RESOLVER = Identifier.fromNamespaceAndPath("rpf", "v1");
    public static final Identifier VANILLA_RESOLVER = Identifier.withDefaultNamespace("vanilla");
    private static final Logger log = LoggerFactory.getLogger(RpfResolversManager.class);

    private final Map<Identifier, RpfItemModelResolver> resolvers = new HashMap<>();
    private Identifier currentResolver = DEFAULT_RESOLVER;
    @Nullable
    private Identifier pendingResolver;

    public void register(Identifier id, RpfItemModelResolver resolver) {
        resolvers.put(id, resolver);
    }

    public void resolve(
            ModelUpdateContext context,
            ItemStack stack,
            ItemOwner entity,
            Operation<Void> vanilla
    ) {
        try {
            currentResolver().resolveAndAppendLayer(
                    context,
                    stack,
                    entity,
                    vanilla
            );
        } catch (Exception exception) {
            log.error(
                    "Resolver '{}' failed while resolving '{}'; falling back to vanilla.",
                    currentResolver,
                    context.location(),
                    exception
            );
            vanilla.call(context.renderState(), stack, context.displayContext(), context.level(), entity, context.seed());
        }
    }

    public boolean shouldPlayAnimationOnSwap(ItemStack stack, Operation<Boolean> vanilla) {
        try {
            return currentResolver().shouldPlaySwapAnimation(stack, vanilla);
        } catch (Exception exception) {
            log.error("Resolver '{}' failed while resolving the swap animation; falling back to vanilla.",
                    currentResolver, exception);
            return vanilla.call(stack);
        }
    }

    public float swapAnimationScale(ItemStack stack, Operation<Float> vanilla) {
        try {
            return currentResolver().swapAnimationScale(stack, vanilla);
        } catch (Exception exception) {
            log.error("Resolver '{}' failed while resolving the swap animation scale; falling back to vanilla.",
                    currentResolver, exception);
            return vanilla.call(stack);
        }
    }

    public void clearCaches() {
        for (RpfItemModelResolver resolver : this.resolvers.values()) {
            resolver.rpf$onReload();
        }
    }

    public void setPendingResolver(Identifier pendingResolver) {
        this.pendingResolver = pendingResolver;
    }

    public void applyPendingResolver() {
        if (pendingResolver == null) return;
        if (!resolvers.containsKey(pendingResolver)) {
            log.error("Unknown resolver '{}' in the config; keeping '{}'.", pendingResolver, currentResolver);
            pendingResolver = null;
            return;
        }
        currentResolver = pendingResolver;
        pendingResolver = null;
    }

    public List<Identifier> getAvailable() {
        return new ArrayList<>(resolvers.keySet());
    }

    private RpfItemModelResolver currentResolver() {
        RpfItemModelResolver resolver = resolvers.get(currentResolver);
        if (resolver != null) {
            return resolver;
        }

        RpfItemModelResolver fallback = resolvers.get(DEFAULT_RESOLVER);
        if (fallback == null) {
            throw new IllegalStateException("Default resolver '" + DEFAULT_RESOLVER + "' is not registered");
        }

        log.error("Unknown resolver '{}'; falling back to '{}'.", currentResolver, DEFAULT_RESOLVER);
        currentResolver = DEFAULT_RESOLVER;
        return fallback;
    }

    private RpfResolversManager() {}

    public static RpfResolversManager getInstance() {
        return INSTANCE;
    }

    public Identifier getCurrent() {
        return currentResolver;
    }
}
