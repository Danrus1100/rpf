package com.danrus.rpf.impl;

import com.danrus.rpf.api.TestsResultCollector;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DummyTestsResultsCollector implements TestsResultCollector {
    @Override
    public void pushShift() {}

    @Override
    public void popShift() {}

    @Override
    public void resetShift() {}

    @Override
    public void pushPack(String packName) {}

    @Override
    public void touch(@Nullable Class<?> clazz, String description, TestResultType resultType) {}

    @Override
    public Identifier getModelLocation() {
        return Identifier.fromNamespaceAndPath("rpf", "dummy");
    }

    @Override
    public List<String> getStringsToLog() {
        return List.of();
    }
}
