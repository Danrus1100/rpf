package com.danrus.rpf.api;

import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractTestResultCollector implements TestsResultCollector {
    public final Identifier modelLocation;
    protected int currentShift = 0;
    protected String packName;

    public AbstractTestResultCollector(Identifier modelLocation, String initialPackName) {
        this.modelLocation = modelLocation;
        packName = initialPackName;
    }

    public void pushShift() {
        currentShift++;
    }

    public void popShift() {
        currentShift--;
    }

    public void resetShift() {
        currentShift = 0;
    }

    @Override
    public void pushPack(String packName) {
        this.packName = packName;
    }

    public Identifier getModelLocation() {
        return modelLocation;
    }
}
