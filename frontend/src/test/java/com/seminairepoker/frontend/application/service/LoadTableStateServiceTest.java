package com.seminairepoker.frontend.application.service;

import com.seminairepoker.frontend.application.model.PlayerSeatState;
import com.seminairepoker.frontend.application.model.TableState;
import com.seminairepoker.frontend.application.port.LoadTableStatePort;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoadTableStateServiceTest {

    @Test
    void shouldReturnStateFromProvider_whenLoadInitialState() {
        // Arrange
        TableState expectedState = new TableState(
                "AB123",
                "Turn",
                500,
                List.of("ace_of_spades", "king_of_spades"),
                List.of("queen_of_hearts", "queen_of_diamonds"),
                List.of(new PlayerSeatState(1, "Nina", 1_000, true, true, false))
        );
        LoadTableStatePort provider = new LoadTableStatePort() {
            @Override
            public TableState loadInitialState() {
                return expectedState;
            }

            @Override
            public Runnable subscribe(java.util.function.Consumer<TableState> onTableStateUpdated) {
                return () -> { };
            }
        };
        LoadTableStateService service = new LoadTableStateService(provider);

        // Act
        TableState actualState = service.loadInitialState();

        // Assert
        assertSame(expectedState, actualState);
    }

    @Test
    void shouldThrowException_whenLoadTableStatePortIsNull() {
        // Arrange

        // Act
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new LoadTableStateService(null)
        );

        // Assert
        assertEquals("loadTableStatePort must not be null", exception.getMessage());
    }

    @Test
    void shouldForwardSubscriptionToProvider_whenSubscribeIsCalled() {
        // Arrange
        AtomicReference<TableState> capturedState = new AtomicReference<>();
        AtomicReference<Runnable> capturedUnsubscribe = new AtomicReference<>();
        TableState expectedState = new TableState(
                "AB123",
                "waiting",
                0,
                List.of(),
                List.of(),
                List.of(new PlayerSeatState(1, "Nina", 1_000, true, true, false, true, true))
        );
        LoadTableStatePort provider = new LoadTableStatePort() {
            @Override
            public TableState loadInitialState() {
                return expectedState;
            }

            @Override
            public Runnable subscribe(java.util.function.Consumer<TableState> onTableStateUpdated) {
                onTableStateUpdated.accept(expectedState);
                Runnable unsubscribe = () -> { };
                capturedUnsubscribe.set(unsubscribe);
                return unsubscribe;
            }
        };
        LoadTableStateService service = new LoadTableStateService(provider);

        // Act
        Runnable unsubscribe = service.subscribe(capturedState::set);

        // Assert
        assertSame(expectedState, capturedState.get());
        assertSame(capturedUnsubscribe.get(), unsubscribe);
    }
}
