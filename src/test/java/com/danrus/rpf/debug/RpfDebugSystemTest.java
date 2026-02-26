package com.danrus.rpf.debug;

import com.danrus.rpf.api.TestsResultCollector;
import com.danrus.rpf.impl.DummyTestsResultsCollector;
import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RpfDebugSystemTest {

    private RpfDebugSystem debugSystem;

    @BeforeEach
    void setUp() {
        debugSystem = RpfDebugSystem.getInstance();
        debugSystem.clear();
    }

    @Test
    @DisplayName("getInstance returns same instance")
    void getInstance_returnsSameInstance() {
        RpfDebugSystem instance1 = RpfDebugSystem.getInstance();
        RpfDebugSystem instance2 = RpfDebugSystem.getInstance();
        
        assertSame(instance1, instance2);
    }

    @Test
    @DisplayName("toggleDebug flips debug state")
    void toggleDebug_flipsValue() {
        boolean before = debugSystem.isDebug();
        
        debugSystem.toggleDebug();
        boolean after1 = debugSystem.isDebug();
        
        debugSystem.toggleDebug();
        boolean after2 = debugSystem.isDebug();
        
        assertNotEquals(before, after1);
        assertEquals(before, after2);
    }

    @Test
    @DisplayName("optimiseCollector returns dummy when debug off")
    void optimiseCollector_debugOff_returnsDummy() {
        if (debugSystem.isDebug()) {
            debugSystem.toggleDebug();
        }
        
        TestsResultCollector result = debugSystem.optimiseCollector(
            () -> new LoggingTestsResultCollector(
                Identifier.fromNamespaceAndPath("test", "model"), "pack"
            )
        );
        
        assertTrue(result instanceof DummyTestsResultsCollector);
    }

    @Test
    @DisplayName("optimiseCollector returns real collector when debug on")
    void optimiseCollector_debugOn_returnsReal() {
        if (!debugSystem.isDebug()) {
            debugSystem.toggleDebug();
        }
        
        TestsResultCollector result = debugSystem.optimiseCollector(
            () -> new LoggingTestsResultCollector(
                Identifier.fromNamespaceAndPath("test", "model"), "pack"
            )
        );
        
        assertTrue(result instanceof LoggingTestsResultCollector);
    }

    @Test
    @DisplayName("clear removes all logged items")
    void clear_clearsMaps() {
        if (!debugSystem.isDebug()) {
            debugSystem.toggleDebug();
        }
        
        LoggingTestsResultCollector collector = new LoggingTestsResultCollector(
            Identifier.fromNamespaceAndPath("test", "model1"), "pack"
        );
        collector.touch(Object.class, "test", TestsResultCollector.TestResultType.INFO);
        
        debugSystem.logItem(collector);
        debugSystem.errorItem(collector);
        
        assertFalse(debugSystem.getDatabaseKeys().isEmpty());
        
        debugSystem.clear();
        
        assertTrue(debugSystem.getDatabaseKeys().isEmpty());
    }

    @Test
    @DisplayName("getDatabaseKeys returns empty list when nothing logged")
    void getDatabaseKeys_whenEmpty_returnsEmptyList() {
        debugSystem.clear();
        
        List<Identifier> keys = debugSystem.getDatabaseKeys();
        
        assertTrue(keys.isEmpty());
    }

    @Test
    @DisplayName("getDatabaseKeys returns sorted distinct keys")
    void getDatabaseKeys_returnsSortedDistinctKeys() {
        if (!debugSystem.isDebug()) {
            debugSystem.toggleDebug();
        }
        
        Identifier loc1 = Identifier.fromNamespaceAndPath("test", "a_model");
        Identifier loc2 = Identifier.fromNamespaceAndPath("test", "b_model");
        Identifier loc3 = Identifier.fromNamespaceAndPath("test", "a_model");
        
        LoggingTestsResultCollector collector1 = new LoggingTestsResultCollector(loc1, "pack");
        LoggingTestsResultCollector collector2 = new LoggingTestsResultCollector(loc2, "pack");
        LoggingTestsResultCollector collector3 = new LoggingTestsResultCollector(loc3, "pack");
        
        debugSystem.logItem(collector1);
        debugSystem.errorItem(collector2);
        debugSystem.logItem(collector3);
        
        List<Identifier> keys = debugSystem.getDatabaseKeys();
        
        assertEquals(2, keys.size());
        assertEquals(loc1, keys.get(0));
        assertEquals(loc2, keys.get(1));
    }

    @Test
    @DisplayName("logItem does nothing when debug off")
    void logItem_debugOff_noEffect() {
        if (debugSystem.isDebug()) {
            debugSystem.toggleDebug();
        }
        
        LoggingTestsResultCollector collector = new LoggingTestsResultCollector(
            Identifier.fromNamespaceAndPath("test", "model"), "pack"
        );
        
        debugSystem.logItem(collector);
        
        assertTrue(debugSystem.getDatabaseKeys().isEmpty());
    }

    @Test
    @DisplayName("errorItem does nothing when debug off")
    void errorItem_debugOff_noEffect() {
        if (debugSystem.isDebug()) {
            debugSystem.toggleDebug();
        }
        
        LoggingTestsResultCollector collector = new LoggingTestsResultCollector(
            Identifier.fromNamespaceAndPath("test", "model"), "pack"
        );
        
        debugSystem.errorItem(collector);
        
        assertTrue(debugSystem.getDatabaseKeys().isEmpty());
    }

    @Test
    @DisplayName("Same location logged once")
    void logItem_sameLocation_loggedOnce() {
        if (!debugSystem.isDebug()) {
            debugSystem.toggleDebug();
        }
        
        Identifier location = Identifier.fromNamespaceAndPath("test", "model");
        
        LoggingTestsResultCollector collector1 = new LoggingTestsResultCollector(location, "pack1");
        LoggingTestsResultCollector collector2 = new LoggingTestsResultCollector(location, "pack2");
        
        debugSystem.logItem(collector1);
        debugSystem.logItem(collector2);
        
        List<Identifier> keys = debugSystem.getDatabaseKeys();
        
        assertEquals(1, keys.size());
    }

    @Test
    @DisplayName("exportDump returns null for non-existent location")
    void exportDump_nonExistent_returnsNull() {
        Identifier nonExistent = Identifier.fromNamespaceAndPath("test", "nonexistent");
        
        assertNull(debugSystem.exportDump(nonExistent));
    }
}
