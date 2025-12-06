package com.danrus.rpf.duck;

public interface RpfItemModel {
    void rpf$setFallback();
    boolean rpf$isFallback();

    void rpf$setDeligation(boolean value);
    boolean rpf$delegate();

    void rpf$setFalbackId(int id);
    int rpf$getFallbackId();
}
