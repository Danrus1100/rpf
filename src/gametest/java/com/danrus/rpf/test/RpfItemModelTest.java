package com.danrus.rpf.test;

import com.danrus.rpf.api.RpfItemModel;
import com.danrus.rpf.api.TestsResultCollector;
import com.danrus.rpf.debug.LoggingTestsResultCollector;
import com.danrus.rpf.core.item.ModelUpdateContext;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.client.renderer.item.EmptyModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static org.junit.jupiter.api.Assertions.*;

public class RpfItemModelTest implements FabricClientGameTest {

    @Override
    public void runTest(ClientGameTestContext context) {
        testDefaultImplementation();
        testEmptyModel();
        testFallbackMarking();
        testDelegateWithFallback();
        testDelegateWithoutFallback();
    }

    private void testDefaultImplementation() {
        TestRpfItemModel model = new TestRpfItemModel();
        
        assertFalse(model.rpf$isFallback(), "Should not be fallback by default");
        
        model.rpf$markAsFallback();
        assertTrue(model.rpf$isFallback(), "Should be fallback after marking");
    }

    private void testEmptyModel() {
        ItemModel emptyModel = EmptyModel.INSTANCE;
        
        assertTrue(emptyModel instanceof RpfItemModel, 
            "EmptyModel should implement RpfItemModel");
        
        RpfItemModel rpfModel = (RpfItemModel) emptyModel;
        
        LoggingTestsResultCollector collector = new LoggingTestsResultCollector(
            ResourceLocation.fromNamespaceAndPath("test", "empty"), "test"
        );
        
        boolean result = rpfModel.rpf$doDelegate(null, ItemStack.EMPTY, null, null, collector);
        assertFalse(result, "EmptyModel should not delegate");
    }

    private void testFallbackMarking() {
        TestRpfItemModel model = new TestRpfItemModel();
        
        assertFalse(model.rpf$isFallback());
        
        model.rpf$markAsFallback();
        assertTrue(model.rpf$isFallback());
        
        model.rpf$markAsFallback();
        assertTrue(model.rpf$isFallback(), "Marking again should keep fallback state");
    }

    private void testDelegateWithFallback() {
        TestRpfItemModel model = new TestRpfItemModel();
        model.rpf$markAsFallback();
        
        LoggingTestsResultCollector collector = new LoggingTestsResultCollector(
            ResourceLocation.fromNamespaceAndPath("test", "fallback"), "test"
        );
        
        boolean result = model.rpf$doDelegate(null, ItemStack.EMPTY, null, null, collector);
        
        assertTrue(result, "Should delegate when marked as fallback");
        
        java.util.List<String> logs = collector.getStringsToLog();
        assertFalse(logs.isEmpty(), "Should have logged delegate action");
        assertTrue(logs.get(0).contains("DELEGATE"), "Log should contain DELEGATE");
    }

    private void testDelegateWithoutFallback() {
        TestRpfItemModel model = new TestRpfItemModel();
        
        LoggingTestsResultCollector collector = new LoggingTestsResultCollector(
            ResourceLocation.fromNamespaceAndPath("test", "no_fallback"), "test"
        );
        
        boolean result = model.rpf$doDelegate(null, ItemStack.EMPTY, null, null, collector);
        
        assertFalse(result, "Should not delegate when not marked as fallback");
    }

    private static class TestRpfItemModel implements RpfItemModel {
        private boolean isFallback = false;

        @Override
        public void rpf$markAsFallback() {
            this.isFallback = true;
        }

        @Override
        public boolean rpf$isFallback() {
            return isFallback;
        }
    }
}
