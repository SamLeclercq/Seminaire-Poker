package com.seminairepoker.frontend;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainAppTest {

    @Test
    void shouldExposeWindowTitle_whenClassIsLoaded() {
        // Arrange

        // Act
        String windowTitle = MainApp.WINDOW_TITLE;

        // Assert
        assertEquals("Seminaire Poker - Frontend", windowTitle);
    }

    @Test
    void shouldCreateApplicationInstance_whenInstantiated() {
        // Arrange

        // Act
        MainApp app = new MainApp();

        // Assert
        assertTrue(app instanceof MainApp);
    }
}
