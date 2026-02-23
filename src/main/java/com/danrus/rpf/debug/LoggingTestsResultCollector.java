package com.danrus.rpf.debug;

import com.danrus.rpf.api.AbstractTestResultCollector;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LoggingTestsResultCollector extends AbstractTestResultCollector {

    protected final List<TestResultUnit> units = new LinkedList<>();

    public LoggingTestsResultCollector(ResourceLocation modelLocation, String packName) {
        super(modelLocation, packName);
    }

    @Override
    public void touch(@Nullable Class<?> clazz, String description, TestResultType resultType) {
        units.add(new TestResultUnit(clazz, description, this.packName , resultType, currentShift));
    }

    protected record TestResultUnit(Class<?> clazz, String itemModelType, String packName, TestResultType result, int shift) {

        public String toPrint() {
            String shiftString = "  ".repeat(shift);
            return shiftString + "Pack " + packName + ": action " + (clazz == null ? "" : clazz.getSimpleName()) + String.join(" ", itemModelType, result.toString());
        }
    }

    public List<String> getStringsToLog() {
        List<String> strings = new ArrayList<>(units.size());
        for (TestResultUnit unit : units) {
            strings.add(unit.toPrint());
        }
        return strings;
    }
}
