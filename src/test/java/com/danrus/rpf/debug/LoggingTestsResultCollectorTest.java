package com.danrus.rpf.debug;

import com.danrus.rpf.api.TestsResultCollector;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoggingTestsResultCollectorTest {

    private LoggingTestsResultCollector collector;
    private static final ResourceLocation TEST_LOCATION = ResourceLocation.fromNamespaceAndPath("test", "model");

    @BeforeEach
    void setUp() {
        collector = new LoggingTestsResultCollector(TEST_LOCATION, "test_pack");
    }

    @Test
    @DisplayName("touch creates result unit")
    void touch_createsUnit() {
        collector.touch(String.class, "test description", TestsResultCollector.TestResultType.ALLOW_UPDATE);
        
        List<String> logs = collector.getStringsToLog();
        
        assertEquals(1, logs.size());
        assertTrue(logs.get(0).contains("test_pack"));
        assertTrue(logs.get(0).contains("test description"));
        assertTrue(logs.get(0).contains("ALLOW_UPDATE"));
    }

    @Test
    @DisplayName("hit calls touch with ALLOW_UPDATE type")
    void hit_callsTouchWithAllowUpdate() {
        collector.hit(String.class, "hit description");
        
        List<String> logs = collector.getStringsToLog();
        
        assertEquals(1, logs.size());
        assertTrue(logs.get(0).contains("ALLOW_UPDATE"));
    }

    @Test
    @DisplayName("delegate calls touch with DELEGATE type")
    void delegate_callsTouchWithDelegate() {
        collector.delegate(String.class, "delegate description");
        
        List<String> logs = collector.getStringsToLog();
        
        assertEquals(1, logs.size());
        assertTrue(logs.get(0).contains("DELEGATE"));
    }

    @Test
    @DisplayName("next with fallback uses NEXT_TEST_FALLBACK")
    void next_withFallback_correctType() {
        collector.next(String.class, "fallback", true);
        
        List<String> logs = collector.getStringsToLog();
        
        assertEquals(1, logs.size());
        assertTrue(logs.get(0).contains("NEXT_TEST_FALLBACK"));
    }

    @Test
    @DisplayName("next without fallback uses NEXT_TEST")
    void next_withoutFallback_correctType() {
        collector.next(String.class, "next", false);
        
        List<String> logs = collector.getStringsToLog();
        
        assertEquals(1, logs.size());
        assertTrue(logs.get(0).contains("NEXT_TEST"));
    }

    @Test
    @DisplayName("info calls touch with INFO type")
    void info_callsTouchWithInfo() {
        collector.info(String.class, "info message");
        
        List<String> logs = collector.getStringsToLog();
        
        assertEquals(1, logs.size());
        assertTrue(logs.get(0).contains("INFO"));
    }

    @Test
    @DisplayName("info without class works")
    void info_withoutClass_works() {
        collector.info("simple info");
        
        List<String> logs = collector.getStringsToLog();
        
        assertEquals(1, logs.size());
        assertTrue(logs.get(0).contains("INFO"));
    }

    @Test
    @DisplayName("touchModelNotFound creates ERROR entry")
    void touchModelNotFound_createsErrorEntry() {
        collector.touchModelNotFound();
        
        List<String> logs = collector.getStringsToLog();
        
        assertEquals(1, logs.size());
        assertTrue(logs.get(0).contains("ERROR"));
        assertTrue(logs.get(0).contains("Not Found"));
    }

    @Test
    @DisplayName("pushShift and popShift affect indentation")
    void pushShift_popShift_affectsIndentation() {
        collector.touch(Object.class, "no shift", TestsResultCollector.TestResultType.INFO);
        
        collector.pushShift();
        collector.touch(Object.class, "one shift", TestsResultCollector.TestResultType.INFO);
        
        collector.pushShift();
        collector.touch(Object.class, "two shifts", TestsResultCollector.TestResultType.INFO);
        
        collector.popShift();
        collector.touch(Object.class, "back to one", TestsResultCollector.TestResultType.INFO);
        
        List<String> logs = collector.getStringsToLog();
        
        assertFalse(logs.get(0).startsWith("  "));
        assertTrue(logs.get(1).startsWith("  "));
        assertTrue(logs.get(1).startsWith("  "));
        assertFalse(logs.get(1).startsWith("    "));
        assertTrue(logs.get(2).startsWith("    "));
        assertTrue(logs.get(3).startsWith("  "));
        assertFalse(logs.get(3).startsWith("    "));
    }

    @Test
    @DisplayName("resetShift sets shift to zero")
    void resetShift_setsShiftToZero() {
        collector.pushShift();
        collector.pushShift();
        collector.pushShift();
        
        collector.resetShift();
        
        collector.touch(Object.class, "after reset", TestsResultCollector.TestResultType.INFO);
        
        List<String> logs = collector.getStringsToLog();
        
        assertFalse(logs.get(0).startsWith("  "));
    }

    @Test
    @DisplayName("pushPack changes pack name")
    void pushPack_changesPackName() {
        collector.pushPack("new_pack");
        collector.touch(Object.class, "test", TestsResultCollector.TestResultType.INFO);
        
        List<String> logs = collector.getStringsToLog();
        
        assertTrue(logs.get(0).contains("new_pack"));
        assertFalse(logs.get(0).contains("test_pack"));
    }

    @Test
    @DisplayName("getModelLocation returns correct location")
    void getModelLocation_returnsCorrectLocation() {
        assertEquals(TEST_LOCATION, collector.getModelLocation());
    }

    @Test
    @DisplayName("Multiple touches create multiple entries")
    void multipleTouches_createMultipleEntries() {
        collector.touch(Object.class, "first", TestsResultCollector.TestResultType.INFO);
        collector.touch(Object.class, "second", TestsResultCollector.TestResultType.INFO);
        collector.touch(Object.class, "third", TestsResultCollector.TestResultType.INFO);
        
        List<String> logs = collector.getStringsToLog();
        
        assertEquals(3, logs.size());
    }

    @Test
    @DisplayName("Class name appears in output")
    void touch_withClass_includesClassName() {
        collector.touch(String.class, "test", TestsResultCollector.TestResultType.INFO);
        
        List<String> logs = collector.getStringsToLog();
        
        assertTrue(logs.get(0).contains("String"));
    }

    @Test
    @DisplayName("Null class does not cause exception")
    void touch_nullClass_noException() {
        assertDoesNotThrow(() -> collector.touch(null, "no class", TestsResultCollector.TestResultType.INFO));
        
        List<String> logs = collector.getStringsToLog();
        assertEquals(1, logs.size());
    }
}
