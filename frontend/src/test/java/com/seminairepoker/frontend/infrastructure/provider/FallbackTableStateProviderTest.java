package com.seminairepoker.frontend.infrastructure.provider;

import com.seminairepoker.frontend.application.model.PlayerSeatState;
import com.seminairepoker.frontend.application.model.TableState;
import com.seminairepoker.frontend.application.port.TableStateProvider;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class FallbackTableStateProviderTest {

    @Test
    void shouldUsePrimaryState_whenPrimaryProviderSucceeds() {
        // Arrange
        TableState primaryState = sampleState("Turn", 520);
        AtomicInteger fallbackCalls = new AtomicInteger(0);

        TableStateProvider primary = providerReturning(primaryState);
        TableStateProvider fallback = new TableStateProvider() {
            @Override
            public TableState loadInitialState() {
                fallbackCalls.incrementAndGet();
                return sampleState("Flop", 240);
            }

            @Override
            public Runnable subscribe(java.util.function.Consumer<TableState> onTableStateUpdated) {
                return () -> { };
            }
        };

        FallbackTableStateProvider provider = new FallbackTableStateProvider(primary, fallback);

        // Act
        TableState state = provider.loadInitialState();

        // Assert
        assertSame(primaryState, state);
        assertEquals(0, fallbackCalls.get());
    }

    @Test
    void shouldUseFallbackState_whenPrimaryProviderFails() {
        // Arrange
        TableState fallbackState = sampleState("River", 900);

        TableStateProvider primary = new TableStateProvider() {
            @Override
            public TableState loadInitialState() {
                throw new IllegalStateException("backend unavailable");
            }

            @Override
            public Runnable subscribe(java.util.function.Consumer<TableState> onTableStateUpdated) {
                return () -> { };
            }
        };
        TableStateProvider fallback = providerReturning(fallbackState);

        FallbackTableStateProvider provider = new FallbackTableStateProvider(primary, fallback);

        // Act
        TableState state = provider.loadInitialState();

        // Assert
        assertSame(fallbackState, state);
    }

    @Test
    void shouldDelegateSubscriptionToPrimaryProvider_whenSubscribeIsCalled() {
        // Arrange
        AtomicReference<TableState> capturedState = new AtomicReference<>();
        TableState expectedState = sampleState("waiting", 0);
        TableStateProvider primary = new TableStateProvider() {
            @Override
            public TableState loadInitialState() {
                return expectedState;
            }

            @Override
            public Runnable subscribe(java.util.function.Consumer<TableState> onTableStateUpdated) {
                onTableStateUpdated.accept(expectedState);
                return () -> { };
            }
        };
        TableStateProvider fallback = providerReturning(sampleState("Turn", 200));
        FallbackTableStateProvider provider = new FallbackTableStateProvider(primary, fallback);

        // Act
        provider.subscribe(capturedState::set);

        // Assert
        assertSame(expectedState, capturedState.get());
    }

    private TableStateProvider providerReturning(TableState state) {
        return new TableStateProvider() {
            @Override
            public TableState loadInitialState() {
                return state;
            }

            @Override
            public Runnable subscribe(java.util.function.Consumer<TableState> onTableStateUpdated) {
                return () -> { };
            }
        };
    }

    private TableState sampleState(String roundLabel, int pot) {
        return new TableState(
                "AB123",
                roundLabel,
                pot,
                List.of("10_of_hearts", "10_of_spades", "2_of_clubs", "3_of_diamonds", "5_of_hearts"),
                List.of("ace_of_spades", "ace_of_hearts"),
                List.of(new PlayerSeatState(1, "Nina", 1_540, false, true, false))
        );
    }
}
