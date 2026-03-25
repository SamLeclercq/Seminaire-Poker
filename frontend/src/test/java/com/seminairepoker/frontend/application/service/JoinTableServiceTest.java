package com.seminairepoker.frontend.application.service;

import com.seminairepoker.frontend.application.port.JoinTablePort;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JoinTableServiceTest {

    @Test
    void should_reject_join_when_table_code_is_invalid() {
        // Arrange
        AtomicInteger invocationCount = new AtomicInteger(0);
        JoinTablePort joinTablePort = code -> {
            invocationCount.incrementAndGet();
            return true;
        };
        JoinTableService joinTableService = new JoinTableService(joinTablePort, new TableCodeValidator());

        // Act
        boolean isJoined = joinTableService.joinTable("AB1");

        // Assert
        assertFalse(isJoined);
        assertEquals(0, invocationCount.get());
    }

    @Test
    void should_join_table_when_table_code_is_valid() {
        // Arrange
        AtomicReference<String> capturedCode = new AtomicReference<>();
        JoinTablePort joinTablePort = code -> {
            capturedCode.set(code);
            return true;
        };
        JoinTableService joinTableService = new JoinTableService(joinTablePort, new TableCodeValidator());

        // Act
        boolean isJoined = joinTableService.joinTable(" ab123 ");

        // Assert
        assertTrue(isJoined);
        assertEquals("AB123", capturedCode.get());
    }
}

