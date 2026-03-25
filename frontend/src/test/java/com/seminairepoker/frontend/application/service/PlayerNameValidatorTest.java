package com.seminairepoker.frontend.application.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerNameValidatorTest {

    @Test
    void should_return_true_when_player_name_is_not_blank() {
        // Arrange
        PlayerNameValidator validator = new PlayerNameValidator();

        // Act
        boolean isValid = validator.isValid("  Baptiste  ");

        // Assert
        assertTrue(isValid);
    }

    @Test
    void should_return_false_when_player_name_is_blank() {
        // Arrange
        PlayerNameValidator validator = new PlayerNameValidator();

        // Act
        boolean isValid = validator.isValid("   ");

        // Assert
        assertFalse(isValid);
    }
}

