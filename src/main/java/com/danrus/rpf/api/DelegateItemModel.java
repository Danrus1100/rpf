package com.danrus.rpf.api;

/**
 * An interface for item models that can delegate their rendering to other packs
 */
public interface DelegateItemModel {
    boolean rpf$getDelegation();
    void rpf$setDeligation(boolean value);

    interface Unbaked {
        boolean rpf$getDelegation();
        void rpf$setDeligation(boolean value);
    }
}
