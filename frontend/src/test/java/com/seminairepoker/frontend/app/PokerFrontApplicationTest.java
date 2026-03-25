package com.seminairepoker.frontend.app;

import com.seminairepoker.frontend.application.model.TableState;
import com.seminairepoker.frontend.application.port.JoinTablePort;
import com.seminairepoker.frontend.application.service.LoadTableStateService;
import com.seminairepoker.frontend.infrastructure.provider.FallbackTableStateProvider;
import com.seminairepoker.frontend.infrastructure.provider.InMemoryTableStateProvider;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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
    void shouldCreateFallbackProvider_whenBootstrappingApplicationStateProvider() {
        // Arrange

        // Act
        var provider = PokerFrontApplication.createTableStateProvider();

        // Assert
        assertInstanceOf(FallbackTableStateProvider.class, provider);
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
    void shouldAllowJoinOnlyForKnownCodes_whenUsingDefaultJoinPortFactory() {
        // Arrange
        JoinTablePort joinTablePort = PokerFrontApplication.createJoinTablePort(Set.of("AB123"));

        // Act
        boolean joinsKnownTable = joinTablePort.joinTable("AB123");
        boolean joinsUnknownTable = joinTablePort.joinTable("ZZ999");

        // Assert
        assertTrue(joinsKnownTable);
        assertFalse(joinsUnknownTable);
    }
}
