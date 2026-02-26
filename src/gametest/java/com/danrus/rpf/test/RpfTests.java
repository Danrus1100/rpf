package com.danrus.rpf.test;

import com.danrus.rpf.debug.RpfDebugSystem;
import com.danrus.rpf.core.item.RpfResolversManager;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;

import static org.junit.jupiter.api.Assertions.*;

public class RpfTests implements FabricClientGameTest {
    
    @Override
    public void runTest(ClientGameTestContext context) {
        testResolversManager();
        testDebugSystem();
    }

    private void testResolversManager() {
        RpfResolversManager manager = RpfResolversManager.getInstance();
        
        assertNotNull(manager);
        assertNotNull(manager.getCurrent());
        assertFalse(manager.getAvailable().isEmpty());
        assertTrue(manager.getAvailable().contains(RpfResolversManager.DEFAULT_RESOLVER));
        assertTrue(manager.getAvailable().contains(RpfResolversManager.VANILLA_RESOLVER));
    }

    private void testDebugSystem() {
        RpfDebugSystem debugSystem = RpfDebugSystem.getInstance();
        
        assertNotNull(debugSystem);
        assertNotNull(debugSystem.getDatabaseKeys());
    }
}
