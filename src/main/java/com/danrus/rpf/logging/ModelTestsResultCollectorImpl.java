package com.danrus.rpf.logging;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ModelTestsResultCollectorImpl implements ModelTestsResultCollector {
    private final List<TestResultUnit> units = new LinkedList<>();
    public final ResourceLocation modelLocation;
    private int currentShift = 0;

    public ModelTestsResultCollectorImpl(ResourceLocation modelLocation) {
        this.modelLocation = modelLocation;
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

    public void touch(String itemModelType, String packName, ModelTestsResultCollector.TestResultType resultType) {
        units.add(new TestResultUnit(itemModelType, packName, resultType, currentShift));
    }

    public ResourceLocation getModelLocation() {
        return modelLocation;
    }

    public List<String> getStringsToLog() {
        List<String> strings = new ArrayList<>(units.size());
        for (TestResultUnit unit : units) {
            strings.add(unit.toPrint());
        }
        return strings;
    }

    private record TestResultUnit(String itemModelType, String packName, ModelTestsResultCollector.TestResultType result, int shift) {

        public String toPrint() {
            String shiftString = "  ".repeat(shift);
            return shiftString + "Pack " + packName + ": action " + String.join(" ", itemModelType, result.toString());
        }
    }
}
