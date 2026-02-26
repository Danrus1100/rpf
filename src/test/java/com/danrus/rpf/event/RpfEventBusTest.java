package com.danrus.rpf.event;

import com.danrus.rpf.api.event.RpfEvent;
import com.danrus.rpf.api.event.RpfEventBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class RpfEventBusTest {

    private RpfEventBus eventBus;

    @BeforeEach
    void setUp() {
        eventBus = new RpfEventBus();
    }

    @Test
    @DisplayName("Single listener receives event when posted")
    void register_singleListener_postReceivesEvent() {
        AtomicBoolean received = new AtomicBoolean(false);
        
        eventBus.register(TestEvent.class, e -> received.set(true));
        eventBus.post(new TestEvent());
        
        assertTrue(received.get());
    }

    @Test
    @DisplayName("Multiple listeners all receive event")
    void register_multipleListeners_allReceiveEvent() {
        AtomicInteger counter = new AtomicInteger(0);
        
        eventBus.register(TestEvent.class, e -> counter.incrementAndGet());
        eventBus.register(TestEvent.class, e -> counter.incrementAndGet());
        eventBus.register(TestEvent.class, e -> counter.incrementAndGet());
        
        eventBus.post(new TestEvent());
        
        assertEquals(3, counter.get());
    }

    @Test
    @DisplayName("Exception in one listener does not prevent others from executing")
    void register_listenerThrowsException_otherListenersStillExecute() {
        AtomicBoolean secondListenerExecuted = new AtomicBoolean(false);
        
        eventBus.register(TestEvent.class, e -> { throw new RuntimeException("Test exception"); });
        eventBus.register(TestEvent.class, e -> secondListenerExecuted.set(true));
        
        eventBus.post(new TestEvent());
        
        assertTrue(secondListenerExecuted.get());
    }

    @Test
    @DisplayName("Cancelled event has isCancelled returning true")
    void post_cancelledEvent_eventIsCancelled() {
        AtomicBoolean eventCancelled = new AtomicBoolean(false);
        
        eventBus.register(TestEvent.class, e -> {
            e.cancel();
            eventCancelled.set(e.isCancelled());
        });
        
        eventBus.post(new TestEvent());
        
        assertTrue(eventCancelled.get());
    }

    @Test
    @DisplayName("Posting event with no registered listeners does not throw exception")
    void post_noRegisteredListeners_noException() {
        assertDoesNotThrow(() -> eventBus.post(new TestEvent()));
    }

    @Test
    @DisplayName("Different event types are handled separately")
    void register_differentEventTypes_separateHandling() {
        AtomicBoolean testEvent1Received = new AtomicBoolean(false);
        AtomicBoolean testEvent2Received = new AtomicBoolean(false);
        
        eventBus.register(TestEvent.class, e -> testEvent1Received.set(true));
        eventBus.register(AnotherTestEvent.class, e -> testEvent2Received.set(true));
        
        eventBus.post(new TestEvent());
        
        assertTrue(testEvent1Received.get());
        assertFalse(testEvent2Received.get());
    }

    @Test
    @DisplayName("Event data is passed correctly to listener")
    void post_eventWithData_dataReceivedCorrectly() {
        final String expectedData = "test_data";
        final String[] receivedData = new String[1];
        
        eventBus.register(TestEventWithData.class, e -> receivedData[0] = e.getData());
        
        eventBus.post(new TestEventWithData(expectedData));
        
        assertEquals(expectedData, receivedData[0]);
    }

    static class TestEvent extends RpfEvent {}

    static class AnotherTestEvent extends RpfEvent {}

    static class TestEventWithData extends RpfEvent {
        private final String data;

        TestEventWithData(String data) {
            this.data = data;
        }

        public String getData() {
            return data;
        }
    }
}
