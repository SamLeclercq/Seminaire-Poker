package com.seminairepoker.frontend.application.service;

import com.seminairepoker.frontend.application.port.PlayerActionPort;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayTurnActionServiceTest {

    @Test
    void shouldNormalizeTableCodeAndForwardBet_whenArgumentsAreValid() {
        // Arrange
        AtomicInteger capturedAmount = new AtomicInteger(0);
        StringBuilder capturedTableCode = new StringBuilder();
        PlayTurnActionService service = new PlayTurnActionService(new PlayerActionPort() {
            @Override
            public boolean check(String tableCode) {
                return false;
            }

            @Override
            public boolean fold(String tableCode) {
                return false;
            }

            @Override
            public boolean bet(String tableCode, int amount) {
                capturedTableCode.append(tableCode);
                capturedAmount.set(amount);
                return true;
            }

            @Override
            public boolean raise(String tableCode, int amount) {
                return false;
            }
        }, new TableCodeValidator());

        // Act
        boolean result = service.bet("ab123", 60);

        // Assert
        assertTrue(result);
        assertEquals("AB123", capturedTableCode.toString());
        assertEquals(60, capturedAmount.get());
    }

    @Test
    void shouldRejectRaise_whenAmountIsNotPositive() {
        // Arrange
        PlayTurnActionService service = new PlayTurnActionService(new PlayerActionPort() {
            @Override
            public boolean check(String tableCode) {
                return false;
            }

            @Override
            public boolean fold(String tableCode) {
                return false;
            }

            @Override
            public boolean bet(String tableCode, int amount) {
                return false;
            }

            @Override
            public boolean raise(String tableCode, int amount) {
                return true;
            }
        }, new TableCodeValidator());

        // Act
        boolean result = service.raise("AB123", 0);

        // Assert
        assertFalse(result);
    }
}

