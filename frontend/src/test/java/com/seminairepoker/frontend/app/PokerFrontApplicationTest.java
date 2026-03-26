package com.seminairepoker.frontend.app;

import com.seminairepoker.frontend.application.model.TableState;
import com.seminairepoker.frontend.application.service.LoadTableStateService;
import com.seminairepoker.frontend.infrastructure.provider.InMemoryTableStateProvider;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    void shouldLoadInitialStateWithSixSeatsAndTwoPlayerCards_whenUsingInMemoryProvider() {
        // Arrange
        LoadTableStateService loadTableStateService = new LoadTableStateService(new InMemoryTableStateProvider());

        // Act
        TableState state = loadTableStateService.loadInitialState();

        // Assert
        assertEquals("LOCAL", state.tableCode());
        assertEquals(6, state.seats().size());
        assertEquals(2, state.localPlayerCards().size());
        assertFalse(state.communityCards().isEmpty());
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
