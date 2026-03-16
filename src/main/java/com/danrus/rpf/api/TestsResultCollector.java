package com.danrus.rpf.api;

import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface TestsResultCollector {

    default void touchModelNotFound() {
        touch(null, "Not Found", TestsResultCollector.TestResultType.ERROR);
    }

    default void hit(Class<?> clazz, String description) {
        touch(clazz, description, TestsResultCollector.TestResultType.ALLOW_UPDATE);
    }

    default void delegate(Class<?> clazz, String description) {
        touch(clazz, description, TestsResultCollector.TestResultType.DELEGATE);
    }

    default void next(Class<?> clazz, String description, boolean fallback) {
        if ( fallback ) { touch(clazz, description, TestsResultCollector.TestResultType.NEXT_TEST_FALLBACK); }
        else { touch(clazz, description, TestsResultCollector.TestResultType.NEXT_TEST); }
    }

    default void next(Class<?> clazz, String description) {
        touch(clazz, description, TestsResultCollector.TestResultType.NEXT_TEST);
    }

    default void info(Class<?> clazz, String description) {
        touch(clazz, description, TestsResultCollector.TestResultType.INFO);
    }

    default void info(String description) {
        touch(null, description, TestsResultCollector.TestResultType.INFO);
    }

    void pushShift();
    void popShift();
    void resetShift();

    void pushPack(String packName);

    void touch(@Nullable Class<?> clazz, String description, TestsResultCollector.TestResultType resultType);

    ResourceLocation getModelLocation();

    List<String> getStringsToLog();

    public enum TestResultType {
        ALLOW_UPDATE,
        DELEGATE,
        NEXT_TEST,
        NEXT_TEST_FALLBACK,
        INFO,
        ERROR
    }
}
