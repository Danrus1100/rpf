package com.danrus.rpf.logging;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ModelTestsResultCollector {
    private final List<TestResultUnit> units = new LinkedList<>();

    public void touchModelNotFound(ResourceLocation location) {
        touch("Not Found", "Unknown", location, TestResultType.ERROR);
    }

    public void touchNextFallback(String itemModelType, String packName, ResourceLocation location) {
        touch(itemModelType, packName, location, TestResultType.NEXT_TEST_FALLBACK);
    }

    public void touchAllow(String itemModelType, String packName, ResourceLocation location) {
        touch(itemModelType, packName, location, TestResultType.ALLOW_UPDATE);
    }

    public void touchDelegate(String itemModelType, String packName, ResourceLocation location) {
        touch(itemModelType, packName, location, TestResultType.DELEGATE);
    }

    public void touchNext(String itemModelType, String packName, ResourceLocation location, boolean fallback) {
        if ( fallback ) { touch(itemModelType, packName, location, TestResultType.NEXT_TEST_FALLBACK); }
        else {touch(itemModelType, packName, location, TestResultType.NEXT_TEST);}
    }

    public void touchNext(String itemModelType, String packName, ResourceLocation location) {
        touch(itemModelType, packName, location, TestResultType.NEXT_TEST);
    }

    private void touch(String itemModelType, String packName, ResourceLocation location, TestResultType resultType) {
        units.add(new TestResultUnit(itemModelType, packName, location, resultType));
    }

    public ResourceLocation getModelLocation() { // FIXME: hack, i made this under my beer
        return units.getFirst().itemModel();
    }

    public List<String> getStringsToLog() {
        List<String> strings = new ArrayList<>(units.size());
        for (TestResultUnit unit : units) {
            strings.add(unit.toPrint());
        }
        return strings;
    }

    private record TestResultUnit(String itemModelType, String packName, ResourceLocation itemModel, TestResultType result) {
        public String toPrint() {
            return "Pack " + packName + ": action " + String.join(" ", itemModelType, itemModel.toString(), result.toString());
        }
    }

    private enum TestResultType {
        ALLOW_UPDATE,
        DELEGATE,
        NEXT_TEST,
        NEXT_TEST_FALLBACK,
        ERROR
    }
}
