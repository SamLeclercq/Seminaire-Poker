package com.seminairepoker.frontend.presentation.view;

import com.seminairepoker.frontend.support.FxUiTestSupport;
import javafx.scene.control.Label;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PotViewTest extends FxUiTestSupport {

    @Test
    void shouldDisplayRoundAndFormattedPot_whenConstructed() throws Exception {
        // Arrange

        // Act
        List<String> texts = onFxThread(() -> {
            PotView potView = new PotView(2_450, "River");
            return potView.getChildren()
                    .stream()
                    .map(node -> ((Label) node).getText())
                    .toList();
        });

        // Assert
        assertAll(
                () -> assertEquals("Round: River", texts.get(0)),
                () -> assertEquals("Pot:2450EUR", normalizeCurrency(texts.get(1)))
        );
    }

    private String normalizeCurrency(String text) {
        return text
                .replace("€", "EUR")
                .replaceAll("[\\s\\u00A0\\u202F]", "");
    }
}

