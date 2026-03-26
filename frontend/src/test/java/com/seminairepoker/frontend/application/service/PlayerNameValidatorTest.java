package com.seminairepoker.frontend.application.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerNameValidatorTest {

    @Test
    void shouldReturnTrue_whenPlayerNameIsNotBlank() {
        // Arrange
        PlayerNameValidator validator = new PlayerNameValidator();

        // Act
        boolean isValid = validator.isValid("  Baptiste  ");

        // Assert
        assertTrue(isValid);
    }

    @Test
    void shouldReturnFalse_whenPlayerNameIsBlank() {
        // Arrange
        PlayerNameValidator validator = new PlayerNameValidator();

        // Act
        boolean isValid = validator.isValid("   ");

        // Assert
        assertFalse(isValid);
    }
}

