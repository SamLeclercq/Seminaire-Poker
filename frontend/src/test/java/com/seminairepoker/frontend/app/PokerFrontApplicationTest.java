package com.seminairepoker.frontend.app;

import com.seminairepoker.frontend.application.model.PlayerSeatState;
import com.seminairepoker.frontend.application.model.TableState;
import com.seminairepoker.frontend.application.port.LoadTableStatePort;
import com.seminairepoker.frontend.application.service.LoadTableStateService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PokerFrontApplicationTest {

    @Test
    void shouldExposeWindowTitle_whenClassIsLoaded() {
        // Arrange

        // Act
        String windowTitle = PokerFrontApplication.WINDOW_TITLE;

        // Assert
        assertEquals("Seminaire Poker - Table", windowTitle);
    }

    @Test
    void shouldReturnConfiguredInitialState_whenLoadingTableState() {
        // Arrange
        TableState expectedState = new TableState(
                "LOCAL",
                "Flop",
                240,
                List.of("10_of_hearts", "10_of_spades", "2_of_clubs", "3_of_diamonds", "5_of_hearts"),
                List.of("ace_of_spades", "ace_of_hearts"),
                List.of(
                        new PlayerSeatState(1, "Nina", 1_540, false, true, false),
                        new PlayerSeatState(2, "Leo", 2_240, true, true, false),
                        new PlayerSeatState(3, "Yuki", 980, false, true, true),
                        new PlayerSeatState(4, "Mara", 1_760, false, true, false),
                        new PlayerSeatState(5, "Khan", 1_320, false, true, false),
                        new PlayerSeatState(6, "Iris", 2_100, false, true, false)
                )
        );
        LoadTableStateService loadTableStateService = new LoadTableStateService(new LoadTableStatePort() {
            @Override
            public TableState loadInitialState() {
                return expectedState;
            }

            @Override
            public Runnable subscribe(java.util.function.Consumer<TableState> onTableStateUpdated) {
                return () -> { };
            }
        });

        // Act
        TableState state = loadTableStateService.loadInitialState();

        // Assert
        assertEquals(expectedState, state);
    }


    @Test
    void shouldExecuteResetBeforeHomeNavigation_whenReturningToHomePage() {
        // Arrange
        AtomicBoolean resetCalled = new AtomicBoolean(false);
        AtomicBoolean homeNavigationCalled = new AtomicBoolean(false);
        AtomicInteger executionOrder = new AtomicInteger(0);

        Runnable returnHomeAction = PokerFrontApplication.createReturnHomeAction(
                () -> {
                    resetCalled.set(true);
                    executionOrder.compareAndSet(0, 1);
                },
                () -> {
                    homeNavigationCalled.set(true);
                    executionOrder.compareAndSet(1, 2);
                }
        );

        // Act
        returnHomeAction.run();

        // Assert
        assertTrue(resetCalled.get());
        assertTrue(homeNavigationCalled.get());
        assertEquals(2, executionOrder.get());
    }
}
