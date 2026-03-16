package com.danrus.rpf.api;

import com.danrus.rpf.debug.LoggingTestsResultCollector;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RpfItemModelTest {

    private TestRpfItemModel model;
    private LoggingTestsResultCollector collector;

    @BeforeEach
    void setUp() {
        model = new TestRpfItemModel();
        collector = new LoggingTestsResultCollector(
            ResourceLocation.fromNamespaceAndPath("test", "model"), "test_pack"
        );
    }

    @Test
    @DisplayName("rpf$isFallback returns false by default")
    void isFallback_default_false() {
        assertFalse(model.rpf$isFallback());
    }

    @Test
    @DisplayName("rpf$markAsFallback sets fallback to true")
    void markAsFallback_setsTrue() {
        model.rpf$markAsFallback();
        assertTrue(model.rpf$isFallback());
    }

    @Test
    @DisplayName("rpf$markAsFallback is idempotent")
    void markAsFallback_idempotent() {
        model.rpf$markAsFallback();
        model.rpf$markAsFallback();
        assertTrue(model.rpf$isFallback());
    }

    @Test
    @DisplayName("rpf$doDelegate returns false when not fallback")
    void doDelegate_notFallback_returnsFalse() {
        boolean result = model.rpf$doDelegate(null, null, null, null, collector);
        assertFalse(result);
    }

    @Test
    @DisplayName("rpf$doDelegate returns true when fallback")
    void doDelegate_isFallback_returnsTrue() {
        model.rpf$markAsFallback();
        boolean result = model.rpf$doDelegate(null, null, null, null, collector);
        assertTrue(result);
    }

    @Test
    @DisplayName("rpf$doDelegate logs delegate action when fallback")
    void doDelegate_isFallback_logsDelegate() {
        model.rpf$markAsFallback();
        model.rpf$doDelegate(null, null, null, null, collector);
        
        var logs = collector.getStringsToLog();
        assertEquals(1, logs.size());
        assertTrue(logs.get(0).contains("DELEGATE"));
    }

    @Test
    @DisplayName("rpf$doDelegate does not log when not fallback")
    void doDelegate_notFallback_noLog() {
        model.rpf$doDelegate(null, null, null, null, collector);
        
        var logs = collector.getStringsToLog();
        assertTrue(logs.isEmpty());
    }

    @Test
    @DisplayName("Multiple models can be marked independently")
    void multipleModels_independentState() {
        TestRpfItemModel model1 = new TestRpfItemModel();
        TestRpfItemModel model2 = new TestRpfItemModel();
        
        model1.rpf$markAsFallback();
        
        assertTrue(model1.rpf$isFallback());
        assertFalse(model2.rpf$isFallback());
    }

    @Test
    @DisplayName("rpf$doDelegate with null collector throws when fallback")
    void doDelegate_nullCollector_whenFallback() {
        model.rpf$markAsFallback();
        assertThrows(NullPointerException.class, () -> 
            model.rpf$doDelegate(null, null, null, null, null)
        );
    }

    @Test
    @DisplayName("rpf$doDelegate with null collector does not throw when not fallback")
    void doDelegate_nullCollector_whenNotFallback() {
        assertDoesNotThrow(() -> 
            model.rpf$doDelegate(null, null, null, null, null)
        );
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
