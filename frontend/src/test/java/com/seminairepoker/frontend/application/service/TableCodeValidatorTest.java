package com.seminairepoker.frontend.application.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TableCodeValidatorTest {

    @Test
    void should_return_true_when_table_code_has_exactly_five_characters() {
        // Arrange
        TableCodeValidator validator = new TableCodeValidator();

        // Act
        boolean isValid = validator.isValid("AB123");

        // Assert
        assertTrue(isValid);
    }

    @Test
    void should_return_false_when_table_code_has_less_than_five_characters() {
        // Arrange
        TableCodeValidator validator = new TableCodeValidator();

        // Act
        boolean isValid = validator.isValid("AB12");

        // Assert
        assertFalse(isValid);
    }

    @Test
    void should_return_false_when_table_code_has_more_than_five_characters() {
        // Arrange
        TableCodeValidator validator = new TableCodeValidator();

        // Act
        boolean isValid = validator.isValid("AB1234");

        // Assert
        assertFalse(isValid);
    }
}

