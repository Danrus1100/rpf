package com.danrus.rpf.logging;

import com.danrus.rpf.api.TestsResultCollector;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class DummyTestsResultsCollector implements TestsResultCollector {
    @Override
    public void pushShift() {}

    @Override
    public void popShift() {}

    @Override
    public void resetShift() {}

    @Override
    public void touch(Class<?> clazz, String itemModelType, String packName, TestResultType resultType) {}

    @Override
    public ResourceLocation getModelLocation() {
        return ResourceLocation.fromNamespaceAndPath("rpf", "dummy");
    }

    @Override
    public List<String> getStringsToLog() {
        return List.of();
    }
}
