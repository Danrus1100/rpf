package com.danrus.rpf.test;

import com.danrus.rpf.api.RpfItemModelResolver;
import com.danrus.rpf.core.item.RpfResolversManager;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RpfResolversManagerTest implements FabricClientGameTest {

    @Override
    public void runTest(ClientGameTestContext context) {
        testGetInstance();
        testDefaultResolver();
        testVanillaResolver();
        testGetAvailable();
        testSetPendingResolver();
        testApplyPendingResolver();
    }

    private void testGetInstance() {
        RpfResolversManager instance1 = RpfResolversManager.getInstance();
        RpfResolversManager instance2 = RpfResolversManager.getInstance();
        
        assertSame(instance1, instance2, "RpfResolversManager should be a singleton");
    }

    private void testDefaultResolver() {
        RpfResolversManager manager = RpfResolversManager.getInstance();
        
        assertNotNull(manager.getCurrent(), "Current resolver should not be null");
        assertEquals(
            RpfResolversManager.DEFAULT_RESOLVER,
            manager.getCurrent(),
            "Default resolver should be rpf:v1"
        );
    }

    private void testVanillaResolver() {
        RpfResolversManager manager = RpfResolversManager.getInstance();
        List<ResourceLocation> available = manager.getAvailable();
        
        assertTrue(
            available.contains(RpfResolversManager.VANILLA_RESOLVER),
            "Vanilla resolver should be available"
        );
    }

    private void testGetAvailable() {
        RpfResolversManager manager = RpfResolversManager.getInstance();
        List<ResourceLocation> available = manager.getAvailable();
        
        assertFalse(available.isEmpty(), "Available resolvers should not be empty");
        assertTrue(
            available.contains(RpfResolversManager.DEFAULT_RESOLVER),
            "Default resolver should be in available list"
        );
    }

    private void testSetPendingResolver() {
        RpfResolversManager manager = RpfResolversManager.getInstance();
        ResourceLocation originalResolver = manager.getCurrent();
        
        ResourceLocation testResolver = RpfResolversManager.VANILLA_RESOLVER;
        manager.setPendingResolver(testResolver);
        
        assertEquals(
            originalResolver,
            manager.getCurrent(),
            "Current resolver should not change after setPendingResolver"
        );
        
        manager.setPendingResolver(originalResolver);
    }

    private void testApplyPendingResolver() {
        RpfResolversManager manager = RpfResolversManager.getInstance();
        ResourceLocation originalResolver = manager.getCurrent();
        
        ResourceLocation testResolver = RpfResolversManager.VANILLA_RESOLVER;
        manager.setPendingResolver(testResolver);
        manager.applyPendingResolver();
        
        assertEquals(
            testResolver,
            manager.getCurrent(),
            "Current resolver should change after applyPendingResolver"
        );
        
        manager.setPendingResolver(originalResolver);
        manager.applyPendingResolver();
    }
}
