package com.danrus.rpf.core.load;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Holds the future of the item-model resources for the reload that is currently in progress.
 *
 * <p>Access is atomic; callers must not assume that the future belongs to the reload they
 * are currently handling if two reloads overlap.</p>
 */
public class ResourceLoadManager {

    private final AtomicReference<CompletableFuture<List<RpfClientItemInfoLoader.LoadedClientInfos>>>
            currentFuture = new AtomicReference<>();

    public CompletableFuture<List<RpfClientItemInfoLoader.LoadedClientInfos>> getCurrentFuture() {
        return currentFuture.get();
    }

    public void setCurrentFuture(CompletableFuture<List<RpfClientItemInfoLoader.LoadedClientInfos>> future) {
        currentFuture.set(future);
    }
}
