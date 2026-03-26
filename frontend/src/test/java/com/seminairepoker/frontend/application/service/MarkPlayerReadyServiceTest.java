package com.seminairepoker.frontend.application.service;

import com.seminairepoker.frontend.application.port.ReadyPort;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarkPlayerReadyServiceTest {

    @Test
    void shouldRejectReady_whenTableCodeIsInvalid() {
        // Arrange
        AtomicInteger invocationCount = new AtomicInteger(0);
        ReadyPort readyPort = code -> {
            invocationCount.incrementAndGet();
            return true;
        };
        MarkPlayerReadyService service = new MarkPlayerReadyService(readyPort, new TableCodeValidator());

        // Act
        boolean markedReady = service.markReady("A1");

        // Assert
        assertFalse(markedReady);
        assertEquals(0, invocationCount.get());
    }

    @Test
    void shouldForwardNormalizedCode_whenTableCodeIsValid() {
        // Arrange
        AtomicReference<String> capturedCode = new AtomicReference<>();
        ReadyPort readyPort = code -> {
            capturedCode.set(code);
            return true;
        };
        MarkPlayerReadyService service = new MarkPlayerReadyService(readyPort, new TableCodeValidator());

        // Act
        boolean markedReady = service.markReady(" ab123 ");

        // Assert
        assertTrue(markedReady);
        assertEquals("AB123", capturedCode.get());
    }
}

