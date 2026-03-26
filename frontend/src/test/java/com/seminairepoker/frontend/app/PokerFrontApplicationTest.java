package com.seminairepoker.frontend.app;

import com.seminairepoker.frontend.application.model.TableState;
import com.seminairepoker.frontend.application.service.LoadTableStateService;
import com.seminairepoker.frontend.infrastructure.provider.InMemoryTableStateProvider;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

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
    void should_reset_local_navigation_state_when_returning_to_home_page() {
        // Arrange
        AtomicBoolean resetCalled = new AtomicBoolean(false);
        AtomicBoolean homeNavigationCalled = new AtomicBoolean(false);

        Runnable returnHomeAction = PokerFrontApplication.createReturnHomeAction(
                () -> resetCalled.set(true),
                () -> homeNavigationCalled.set(true)
        );

        // Act
        returnHomeAction.run();

        // Assert
        assertTrue(resetCalled.get());
        assertTrue(homeNavigationCalled.get());
    }

    @Test
    void should_not_call_disconnect_when_navigating_back_to_home_page() {
        // Arrange
        AtomicBoolean disconnectCalled = new AtomicBoolean(false);

        Runnable returnHomeAction = PokerFrontApplication.createReturnHomeAction(
                () -> { },
                () -> { }
        );

        // Act
        returnHomeAction.run();

        // Assert
        assertFalse(disconnectCalled.get());
    }
}
