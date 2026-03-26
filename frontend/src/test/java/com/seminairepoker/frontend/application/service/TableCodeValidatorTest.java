package com.seminairepoker.frontend.application.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TableCodeValidatorTest {

    @Test
    void shouldReturnTrue_whenTableCodeHasExactlyFiveCharacters() {
        // Arrange
        TableCodeValidator validator = new TableCodeValidator();

        // Act
        boolean isValid = validator.isValid("AB123");

        // Assert
        assertTrue(isValid);
    }

    @Test
    void shouldReturnFalse_whenTableCodeHasLessThanFiveCharacters() {
        // Arrange
        TableCodeValidator validator = new TableCodeValidator();

        // Act
        boolean isValid = validator.isValid("AB12");

        // Assert
        assertFalse(isValid);
    }

    @Test
    void shouldReturnFalse_whenTableCodeHasMoreThanFiveCharacters() {
        // Arrange
        TableCodeValidator validator = new TableCodeValidator();

        // Act
        boolean isValid = validator.isValid("AB1234");

        // Assert
        assertFalse(isValid);
    }
}

