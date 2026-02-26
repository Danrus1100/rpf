package com.danrus.rpf.impl;

import com.danrus.rpf.api.TestsResultCollector;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DummyTestsResultsCollectorTest {

    private TestsResultCollector collector;

    @BeforeEach
    void setUp() {
        collector = new DummyTestsResultsCollector();
    }

    @Test
    @DisplayName("All methods execute without exceptions")
    void allMethods_noExceptions() {
        assertDoesNotThrow(() -> {
            collector.pushShift();
            collector.popShift();
            collector.resetShift();
            collector.pushPack("any_pack");
            collector.touch(Object.class, "any", TestsResultCollector.TestResultType.ALLOW_UPDATE);
            collector.hit(Object.class, "any");
            collector.delegate(Object.class, "any");
            collector.next(Object.class, "any");
            collector.next(Object.class, "any", true);
            collector.info(Object.class, "any");
            collector.info("any");
            collector.touchModelNotFound();
        });
    }

    @Test
    @DisplayName("getModelLocation returns dummy location")
    void getModelLocation_returnsDummy() {
        ResourceLocation location = collector.getModelLocation();
        
        assertEquals("rpf", location.getNamespace());
        assertEquals("dummy", location.getPath());
    }

    @Test
    @DisplayName("getStringsToLog returns empty list")
    void getStringsToLog_returnsEmptyList() {
        collector.touch(Object.class, "any", TestsResultCollector.TestResultType.ALLOW_UPDATE);
        
        List<String> logs = collector.getStringsToLog();
        
        assertNotNull(logs);
        assertTrue(logs.isEmpty());
    }

    @Test
    @DisplayName("pushShift popShift balance does not matter")
    void pushShift_popShift_balanceIrrelevant() {
        assertDoesNotThrow(() -> {
            collector.pushShift();
            collector.pushShift();
            collector.pushShift();
        });
        
        assertDoesNotThrow(() -> {
            collector.popShift();
            collector.popShift();
            collector.popShift();
            collector.popShift();
        });
    }

    @Test
    @DisplayName("Multiple operations maintain no-op behavior")
    void multipleOperations_maintainNoOp() {
        for (int i = 0; i < 100; i++) {
            collector.touch(Object.class, "test" + i, TestsResultCollector.TestResultType.INFO);
        }
        
        assertTrue(collector.getStringsToLog().isEmpty());
    }
}
