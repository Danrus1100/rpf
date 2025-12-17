package com.danrus.rpf.api;

// item model must implement this if it has fallback
public interface DelegateItemModel {
    boolean rpf$delegate();
    void rpf$setDeligation(boolean value);

    interface Unbaked {
        boolean rpf$delegate();
        void rpf$setDeligation(boolean value);
    }
}
