package com.danrus.rpf.api.event;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Thread-safe event bus for RPF events.
 * 
 * <p>This event bus supports concurrent registration and event posting.
 * Listeners can be registered from any thread, including during event dispatch.
 * 
 * <h2>Thread Safety</h2>
 * This class is fully thread-safe. Uses {@link ConcurrentHashMap} for listener
 * storage and {@link CopyOnWriteArrayList} for listener lists, ensuring safe
 * concurrent access without external synchronization.
 * 
 * @since 1.0.0
 */
public class RpfEventBus {
    // ConcurrentHashMap for thread-safe map operations
    // CopyOnWriteArrayList allows safe concurrent modifications during iteration
    private final Map<Class<? extends RpfEvent>, List<Consumer<? extends RpfEvent>>> listeners = 
        new ConcurrentHashMap<>();

    /**
     * Registers a listener for a specific event type.
     * 
     * <p>This method is thread-safe and can be called from any thread,
     * including during event dispatch.
     * 
     * @param eventClass The class of the event to listen for
     * @param listener The consumer that handles the event
     * @param <T> The type of the event
     * 
     * @since 1.0.0
     */
    public <T extends RpfEvent> void register(Class<T> eventClass, Consumer<T> listener) {
        // computeIfAbsent is atomic in ConcurrentHashMap
        // CopyOnWriteArrayList is thread-safe for concurrent modifications
        listeners.computeIfAbsent(eventClass, k -> new CopyOnWriteArrayList<>()).add(listener);
    }

    /**
     * Posts an event to all registered listeners.
     * 
     * <p>This method is thread-safe. Listeners are invoked synchronously
     * in the order they were registered. If a listener throws an exception,
     * it is logged but does not prevent other listeners from executing.
     * 
     * @param event The event instance to dispatch
     * @param <T> The type of the event
     * 
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public <T extends RpfEvent> void post(T event) {
        List<Consumer<? extends RpfEvent>> eventListeners = listeners.get(event.getClass());
        if (eventListeners != null) {
            // CopyOnWriteArrayList iterator is thread-safe and lock-free
            for (Consumer<? extends RpfEvent> listener : eventListeners) {
                try {
                    ((Consumer<T>) listener).accept(event);
                } catch (Exception e) {
                    // Log exception but continue with other listeners
                    System.err.println("Exception in event listener for " + event.getClass().getSimpleName() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
