package com.danrus.rpf.core.load;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Thread-safe manager for resource loading futures.
 * 
 * <p>This class encapsulates the current resource loading future and provides
 * thread-safe access and updates. It replaces the previous static mutable field
 * which had race condition issues.
 * 
 * <h2>Thread Safety</h2>
 * This class is fully thread-safe. Uses {@link AtomicReference} to ensure atomic
 * updates without external synchronization.
 * 
 * @since 1.4.0
 */
public class ResourceLoadManager {
    
    private final AtomicReference<CompletableFuture<List<RpfClientItemInfoLoader.LoadedClientInfos>>> 
        currentFuture = new AtomicReference<>();
    
    /**
     * Gets the current resource loading future.
     * 
     * @return The current future, or null if no load is in progress
     */
    public CompletableFuture<List<RpfClientItemInfoLoader.LoadedClientInfos>> getCurrentFuture() {
        return currentFuture.get();
    }
    
    /**
     * Sets the current resource loading future.
     * 
     * <p>This operation is atomic and thread-safe.
     * 
     * @param future The new future to set
     */
    public void setCurrentFuture(CompletableFuture<List<RpfClientItemInfoLoader.LoadedClientInfos>> future) {
        currentFuture.set(future);
    }
    
    /**
     * Resets the current future, optionally cancelling it if still running.
     * 
     * <p>This is typically called during resource reload to clean up previous state.
     */
    public void reset() {
        CompletableFuture<List<RpfClientItemInfoLoader.LoadedClientInfos>> current = currentFuture.get();
        if (current != null && !current.isDone()) {
            current.cancel(true);
        }
        currentFuture.set(null);
    }
    
    /**
     * Checks if a load is currently in progress.
     * 
     * @return true if a future exists and is not done yet
     */
    public boolean isLoading() {
        CompletableFuture<List<RpfClientItemInfoLoader.LoadedClientInfos>> current = currentFuture.get();
        return current != null && !current.isDone();
    }
}
