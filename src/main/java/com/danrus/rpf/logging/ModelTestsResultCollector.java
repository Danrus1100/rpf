package com.danrus.rpf.logging;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ModelTestsResultCollector {
    private List<TestResultUnit> units = new LinkedList<>();

    public void touchAllow(String itemModelType, ResourceLocation location) {
        units.add(new TestResultUnit(itemModelType, location, TestResultType.ALLOW_UPDATE));
    }

    public void touchDelegate(String itemModelType, ResourceLocation location) {
        units.add(new TestResultUnit(itemModelType, location, TestResultType.DELEGATE));
    }

    public void touchNext(String itemModelType, ResourceLocation location) {
        units.add(new TestResultUnit(itemModelType, location, TestResultType.NEXT_TEST));
    }

    public List<String> getStringsToLog() {
        List<String> strings = new ArrayList<>(units.size());
        for (TestResultUnit unit : units) {
            strings.add(unit.toPrint());
        }
        return strings;
    }

    private static record TestResultUnit(String itemModelType, ResourceLocation itemModel, TestResultType result) {
        public String toPrint() {
            return String.join(" ", itemModelType, itemModel.toString(), result.toString());
        }
    }

    private enum TestResultType {
        ALLOW_UPDATE,
        DELEGATE,
        NEXT_TEST
    }
}
