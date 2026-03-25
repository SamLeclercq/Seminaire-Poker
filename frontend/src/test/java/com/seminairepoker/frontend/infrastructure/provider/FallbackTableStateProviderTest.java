package com.seminairepoker.frontend.infrastructure.provider;

import com.seminairepoker.frontend.application.port.TableStateProvider;
import com.seminairepoker.frontend.presentation.state.PlayerSeatUiState;
import com.seminairepoker.frontend.presentation.state.TableUiState;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class FallbackTableStateProviderTest {

    @Test
    void shouldUsePrimaryState_whenPrimaryProviderSucceeds() {
        // Arrange
        TableUiState primaryState = sampleState("Turn", 520);
        AtomicInteger fallbackCalls = new AtomicInteger(0);

        TableStateProvider primary = () -> primaryState;
        TableStateProvider fallback = () -> {
            fallbackCalls.incrementAndGet();
            return sampleState("Flop", 240);
        };

        FallbackTableStateProvider provider = new FallbackTableStateProvider(primary, fallback);

        // Act
        TableUiState state = provider.loadInitialState();

        // Assert
        assertSame(primaryState, state);
        assertEquals(0, fallbackCalls.get());
    }

    @Test
    void shouldUseFallbackState_whenPrimaryProviderFails() {
        // Arrange
        TableUiState fallbackState = sampleState("River", 900);

        TableStateProvider primary = () -> {
            throw new IllegalStateException("backend unavailable");
        };
        TableStateProvider fallback = () -> fallbackState;

        FallbackTableStateProvider provider = new FallbackTableStateProvider(primary, fallback);

        // Act
        TableUiState state = provider.loadInitialState();

        // Assert
        assertSame(fallbackState, state);
    }

    private TableUiState sampleState(String roundLabel, int pot) {
        return new TableUiState(
                roundLabel,
                pot,
                List.of("10_of_hearts", "10_of_spades", "2_of_clubs", "3_of_diamonds", "5_of_hearts"),
                List.of("ace_of_spades", "ace_of_hearts"),
                List.of(new PlayerSeatUiState(1, "Nina", 1_540, false, true, false))
        );
    }
}

