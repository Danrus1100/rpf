package com.danrus.rpf.item.items;

import com.danrus.rpf.api.DelegateItemModel;
import com.danrus.rpf.api.RpfItemModel;
import com.danrus.rpf.api.TestsResultCollector;
import com.danrus.rpf.core.item.ModelUpdateContext;
import com.danrus.rpf.debug.LoggingTestsResultCollector;
import net.minecraft.SharedConstants;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.properties.select.ComponentContents;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class RpfSelectItemModelTest {

    private static final Component FALLBACK_NOT_DELEGATE = Component.literal("fallback_not_delegate");
    private static final Component OK = Component.literal("ok");

    @BeforeAll
    static void beforeAll() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        ItemModels.bootstrap();
        SelectItemModelProperties.bootstrap();
    }

    @Test
    void testDelegation_defaultEnabled() {
        SelectItemModel<Component> model = createModel();
        DelegateItemModel delegate = (DelegateItemModel) model;
        
        assertTrue(delegate.rpf$getDelegation(), "Delegation should be enabled by default");
    }
    
    @Test
    void testDelegation_canBeDisabled() {
        SelectItemModel<Component> model = createModel();
        DelegateItemModel delegate = (DelegateItemModel) model;
        
        delegate.rpf$setDeligation(false);
        assertFalse(delegate.rpf$getDelegation());
        
        delegate.rpf$setDeligation(true);
        assertTrue(delegate.rpf$getDelegation());
    }
    
    @Test
    void testDelegationDisabled_returnsFalseAndLogs() {
        SelectItemModel<Component> model = createModel();
        ((DelegateItemModel) model).rpf$setDeligation(false);
        
        LoggingTestsResultCollector collector = createCollector();
        boolean result = ((RpfItemModel) model).rpf$doDelegate(
            createContext(), ItemStack.EMPTY, null, null, collector
        );
        
        assertFalse(result);
        assertFalse(collector.getStringsToLog().isEmpty());
        assertTrue(collector.getStringsToLog().getFirst().contains("force cancel"));
    }
    
    @Test
    void testFallback_defaultFalse() {
        SelectItemModel<Component> model = createModel();
        RpfItemModel rpfModel = (RpfItemModel) model;
        
        assertFalse(rpfModel.rpf$isFallback(), "Should not be fallback by default");
    }
    
    @Test
    void testFallback_canBeMarked() {
        SelectItemModel<Component> model = createModel();
        RpfItemModel rpfModel = (RpfItemModel) model;
        
        rpfModel.rpf$markAsFallback();
        assertTrue(rpfModel.rpf$isFallback());
    }
    
    @Test
    void testFallback_idempotent() {
        SelectItemModel<Component> model = createModel();
        RpfItemModel rpfModel = (RpfItemModel) model;
        
        rpfModel.rpf$markAsFallback();
        rpfModel.rpf$markAsFallback();
        assertTrue(rpfModel.rpf$isFallback());
    }

    @Test
    void testDoDelegate() {

        ModelUpdateContext context = createContext();

        RpfItemModel model = RpfItemModel.class.cast(createModel());
        TestsResultCollector collector = createCollector();

        ItemStack stack = Items.DIAMOND.getDefaultInstance();

        stack.set(DataComponents.CUSTOM_NAME, OK);
        assertFalse(model.rpf$doDelegate(context, stack, null, null, collector),
                "Model should return false when hitting a non-delegate TestItemModel");

        stack.set(DataComponents.CUSTOM_NAME, FALLBACK_NOT_DELEGATE);
        assertFalse(model.rpf$doDelegate(context, stack, null, null, collector),
                "Model should return false for fallback_not_delegate according to your selector logic");

        stack.set(DataComponents.CUSTOM_NAME, Component.empty());
        assertTrue(model.rpf$doDelegate(context, stack, null, null, collector),
                "Model should return true when it's supposed to delegate/fallback to vanilla");
    }

    private SelectItemModel<Component> createModel() {
        ComponentContents<Component> property = new ComponentContents<>(DataComponents.CUSTOM_NAME);

        SelectItemModel.ModelSelector<Component> selector = (object, clientLevel) -> {
            if (object != null) {
                if (FALLBACK_NOT_DELEGATE.equals(object)) return new TestItemModel(false, true);
                if (OK.equals(object)) return new TestItemModel(false, false);
            }
            return new TestItemModel(true, true);
        };

        return new SelectItemModel<>(property, selector);
    }
    
    private LoggingTestsResultCollector createCollector() {
        return new LoggingTestsResultCollector(
            ResourceLocation.fromNamespaceAndPath("rpf", "test"),
            "test_pack"
        );
    }

    private ModelUpdateContext createContext() {
        return new ModelUpdateContext(
                ResourceLocation.fromNamespaceAndPath("rpf", "test"),
                Mockito.mock(ItemStackRenderState.class),
                ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
                null,
                Mockito.mock(ItemModelResolver.class),
                123
        );
    }
}
