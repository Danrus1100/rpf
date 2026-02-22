package com.danrus.rpf.api;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AbstractTestResultCollector implements TestsResultCollector {

    protected final List<TestResultUnit> units = new LinkedList<>();
    public final ResourceLocation modelLocation;
    protected int currentShift = 0;

    public AbstractTestResultCollector(ResourceLocation modelLocation) {
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

    public void touch(Class<?> clazz, String itemModelType, String packName, TestsResultCollector.TestResultType resultType) {
        units.add(new TestResultUnit(clazz, itemModelType, packName, resultType, currentShift));
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

    protected record TestResultUnit(Class<?> clazz, String itemModelType, String packName, TestResultType result, int shift) {

        public String toPrint() {
                String shiftString = "  ".repeat(shift);
                return shiftString + "Pack " + packName + ": action " + (clazz == null ? "" : clazz.getSimpleName()) + String.join(" ", itemModelType, result.toString());
            }
        }
}
