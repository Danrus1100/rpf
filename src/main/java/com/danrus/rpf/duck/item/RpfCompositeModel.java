package com.danrus.rpf.duck.item;

public interface RpfCompositeModel {

    void rpf$setDelegateStrategy(DelegateStrategy strategy);
    DelegateStrategy rpf$getDelegateStrategy();

    public interface Unbaked {
        void rpf$setDelegateStrategy(DelegateStrategy strategy);
        DelegateStrategy rpf$getDelegateStrategy();
    }

    public enum DelegateStrategy {
        ONE_DO_DELEGATE,
        ONE_CANCEL_DELEGATE,
        NOT_DELEGATE
    }
}
