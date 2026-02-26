package com.danrus.rpf.test;

import com.danrus.rpf.debug.RpfDebugSystem;
import com.danrus.rpf.impl.DummyTestsResultsCollector;
import com.danrus.rpf.debug.LoggingTestsResultCollector;
import com.danrus.rpf.api.TestsResultCollector;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.resources.ResourceLocation;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("UnstableApiUsage")
public class RpfDebugSystemGameTest implements FabricClientGameTest {

    @Override
    public void runTest(ClientGameTestContext context) {
        testGetInstance();
        testToggleDebug();
        testOptimiseCollector();
        testClear();
        testExportDump();
    }

    private void testGetInstance() {
        RpfDebugSystem instance1 = RpfDebugSystem.getInstance();
        RpfDebugSystem instance2 = RpfDebugSystem.getInstance();
        
        assertSame(instance1, instance2, "RpfDebugSystem should be a singleton");
    }

    private void testToggleDebug() {
        RpfDebugSystem debugSystem = RpfDebugSystem.getInstance();
        
        boolean before = debugSystem.isDebug();
        debugSystem.toggleDebug();
        boolean after = debugSystem.isDebug();
        
        assertNotEquals(before, after, "Debug state should toggle");
        
        debugSystem.toggleDebug();
        assertEquals(before, debugSystem.isDebug(), "Debug state should return to original");
    }

    private void testOptimiseCollector() {
        RpfDebugSystem debugSystem = RpfDebugSystem.getInstance();
        
        boolean wasDebug = debugSystem.isDebug();
        
        if (debugSystem.isDebug()) {
            debugSystem.toggleDebug();
        }
        
        TestsResultCollector offResult = debugSystem.optimiseCollector(
            () -> new LoggingTestsResultCollector(
                ResourceLocation.fromNamespaceAndPath("test", "model"), "pack"
            )
        );
        assertInstanceOf(DummyTestsResultsCollector.class, offResult, "Should return dummy collector when debug is off");
        
        debugSystem.toggleDebug();
        
        TestsResultCollector onResult = debugSystem.optimiseCollector(
            () -> new LoggingTestsResultCollector(
                ResourceLocation.fromNamespaceAndPath("test", "model"), "pack"
            )
        );
        assertInstanceOf(LoggingTestsResultCollector.class, onResult, "Should return logging collector when debug is on");
        
        if (wasDebug) {
            debugSystem.toggleDebug();
        }
    }

    private void testClear() {
        RpfDebugSystem debugSystem = RpfDebugSystem.getInstance();
        
        debugSystem.clear();
        assertTrue(debugSystem.getDatabaseKeys().isEmpty(), 
            "Database keys should be empty after clear");
    }

    private void testExportDump() {
        RpfDebugSystem debugSystem = RpfDebugSystem.getInstance();
        
        ResourceLocation nonExistent = ResourceLocation.fromNamespaceAndPath("test", "nonexistent");
        assertNull(debugSystem.exportDump(nonExistent), 
            "Export dump should return null for non-existent location");
    }
}
