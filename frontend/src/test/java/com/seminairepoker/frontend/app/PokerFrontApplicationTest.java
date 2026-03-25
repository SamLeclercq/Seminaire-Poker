package com.seminairepoker.frontend.app;

import com.seminairepoker.frontend.application.service.LoadTableStateService;
import com.seminairepoker.frontend.infrastructure.provider.InMemoryTableStateProvider;
import com.seminairepoker.frontend.presentation.state.TableUiState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
        TableUiState state = loadTableStateService.loadInitialState();

        // Assert
        assertEquals(6, state.seats().size());
        assertEquals(2, state.localPlayerCards().size());
        assertFalse(state.communityCards().isEmpty());
    }
}

