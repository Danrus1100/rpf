package com.danrus.rpf.logging;

import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ModelTestsResultCollector {
    private final List<TestResultUnit> units = new LinkedList<>();

    public void touchModelNotFound(Identifier location) {
        touch("Not Found", "Unknown", location, TestResultType.ERROR);
    }

    public void touchAllow(String itemModelType, String packName, Identifier location) {
        touch(itemModelType, packName, location, TestResultType.ALLOW_UPDATE);
    }

    public void touchDelegate(String itemModelType, String packName, Identifier location) {
        touch(itemModelType, packName, location, TestResultType.DELEGATE);
    }

    public void touchNext(String itemModelType, String packName, Identifier location) {
        touch(itemModelType, packName, location, TestResultType.NEXT_TEST);
    }

    private void touch(String itemModelType, String packName, Identifier location, TestResultType resultType) {
        units.add(new TestResultUnit(itemModelType, packName, location, resultType));
    }

    public Identifier getModelLocation() { // FIXME: hack, i made this under my beer
        return units.getFirst().itemModel();
    }

    public List<String> getStringsToLog() {
        List<String> strings = new ArrayList<>(units.size());
        for (TestResultUnit unit : units) {
            strings.add(unit.toPrint());
        }
        return strings;
    }

    private record TestResultUnit(String itemModelType, String packName, Identifier itemModel, TestResultType result) {
        public String toPrint() {
            return "Pack " + packName + ": action " + String.join(" ", itemModelType, itemModel.toString(), result.toString());
        }
    }

    private enum TestResultType {
        ALLOW_UPDATE,
        DELEGATE,
        NEXT_TEST,
        ERROR
    }
}
