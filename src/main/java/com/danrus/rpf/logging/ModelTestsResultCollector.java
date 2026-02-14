package com.danrus.rpf.logging;

import com.danrus.rpf.Rpf;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public interface ModelTestsResultCollector {

    default void touchModelNotFound() {
        touch("Not Found", "Unknown", ModelTestsResultCollector.TestResultType.ERROR);
    }

    default void hit(String itemModelType, String packName) {
        touch(itemModelType, packName, ModelTestsResultCollector.TestResultType.ALLOW_UPDATE);
    }

    default void delegate(String itemModelType, String packName) {
        touch(itemModelType, packName, ModelTestsResultCollector.TestResultType.DELEGATE);
    }

    default void next(String itemModelType, String packName, boolean fallback) {
        if ( fallback ) { touch(itemModelType, packName, ModelTestsResultCollector.TestResultType.NEXT_TEST_FALLBACK); }
        else { touch(itemModelType, packName, ModelTestsResultCollector.TestResultType.NEXT_TEST); }
    }

    default void next(String itemModelType, String packName) {
        touch(itemModelType, packName, ModelTestsResultCollector.TestResultType.NEXT_TEST);
    }

    default void info(String itemModelType, String packName) {
        touch(itemModelType, packName, ModelTestsResultCollector.TestResultType.INFO);
    }


    default void pushShift(){}
    default void popShift(){}
    default void resetShift(){}

    default void touch(String itemModelType, String packName, ModelTestsResultCollector.TestResultType resultType){}

    default ResourceLocation getModelLocation() {
        return ResourceLocation.fromNamespaceAndPath(Rpf.MOD_ID, "dummy");
    }

    default List<String> getStringsToLog() {
        return new ArrayList<>();
    }

    public enum TestResultType {
        ALLOW_UPDATE,
        DELEGATE,
        NEXT_TEST,
        NEXT_TEST_FALLBACK,
        INFO,
        ERROR
    }
}
