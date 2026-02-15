package com.danrus.rpf.api.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class RpfEventBus {
    private final Map<Class<? extends RpfEvent>, List<Consumer<? extends RpfEvent>>> listeners = new HashMap<>();

    /**
     * Registers a listener for a specific event type.
     * @param eventClass The class of the event to listen for
     * @param listener The consumer that handles the event
     * @param <T> The type of the event
     */
    public <T extends RpfEvent> void register(Class<T> eventClass, Consumer<T> listener) {
        listeners.computeIfAbsent(eventClass, k -> new ArrayList<>()).add(listener);
    }

    /**
     * Posts an event to all registered listeners.
     * @param event The event instance to dispatch
     * @param <T> The type of the event
     */
    @SuppressWarnings("unchecked")
    public <T extends RpfEvent> void post(T event) {
        List<Consumer<? extends RpfEvent>> eventListeners = listeners.get(event.getClass());
        if (eventListeners != null) {
            for (Consumer<? extends RpfEvent> listener : eventListeners) {
                ((Consumer<T>) listener).accept(event);
            }
        }
    }
}
