package com.danrus.rpf.core.item;

import com.danrus.rpf.api.RpfItemModelResolver;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
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
        resolvers.get(currentResolver).resolveAndAppendLayer(
                context,
                stack,
                entity,
                vanilla
        );
    }

    public boolean shouldPlayAnimationOnSwap(ItemStack stack, Operation<Boolean> vanilla) {
       return resolvers.get(currentResolver).shouldPlaySwapAnimation(stack, vanilla);
    }

    public void setPendingResolver(Identifier pendingResolver) {
        this.pendingResolver = pendingResolver;
    }

    public void applyPendingResolver() {
        if (pendingResolver == null) return;
        currentResolver = pendingResolver;
        pendingResolver = null;
    }

    public List<Identifier> getAvailable() {
        return new ArrayList<>(resolvers.keySet());
    }

    private RpfResolversManager() {}
    public static RpfResolversManager getInstance() { return INSTANCE; }

    public Identifier getCurrent() {
        return currentResolver;
    }
}
