package com.danrus.rpf.api.event;

public abstract class RpfEvent {
    private boolean cancelled = false;

    /**
     * cancels the event, preventing the default behavior from occurring. This should only be used by events that are explicitly marked as cancellable
     */
    public void cancel() {
        this.cancelled = true;
    }

    /**
     * @return whether the event has been cancelled
     */
    public boolean isCancelled() {
        return cancelled;
    }
}
