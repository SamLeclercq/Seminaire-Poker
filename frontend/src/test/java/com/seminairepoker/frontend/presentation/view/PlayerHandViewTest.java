package com.seminairepoker.frontend.presentation.view;

import com.seminairepoker.frontend.infrastructure.assets.AssetLoader;
import com.seminairepoker.frontend.support.FxUiTestSupport;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerHandViewTest extends FxUiTestSupport {

    @Test
    void shouldRenderExactlyTwoCards_whenOnlyOneCardIsProvided() throws Exception {
        // Arrange
        RecordingAssetLoader assetLoader = new RecordingAssetLoader();

        // Act
        List<String> requestedCodes = onFxThread(() -> {
            new PlayerHandView(List.of("ace_of_hearts"), assetLoader);
            return List.copyOf(assetLoader.requestedCardCodes());
        });

        // Assert
        assertEquals(List.of("ace_of_hearts", "card_face_down"), requestedCodes);
    }

    @Test
    void shouldRenderTwoFaceDownCards_whenNoCardIsProvided() throws Exception {
        // Arrange
        RecordingAssetLoader assetLoader = new RecordingAssetLoader();

        // Act
        List<String> requestedCodes = onFxThread(() -> {
            new PlayerHandView(List.of(), assetLoader);
            return List.copyOf(assetLoader.requestedCardCodes());
        });

        // Assert
        assertEquals(List.of("card_face_down", "card_face_down"), requestedCodes);
    }

    @Test
    void shouldResizeAllCards_whenSetCardSize() throws Exception {
        // Arrange
        RecordingAssetLoader assetLoader = new RecordingAssetLoader();
        PlayerHandView playerHandView = onFxThread(() -> new PlayerHandView(List.of(), assetLoader));

        // Act
        List<Double> widths = onFxThread(() -> {
            playerHandView.setCardSize(120, 180);
            return playerHandView.getChildren()
                    .stream()
                    .map(node -> ((StackPane) node).getPrefWidth())
                    .toList();
        });

        // Assert
        assertTrue(widths.stream().allMatch(width -> width.equals(120.0)));
    }

    private static final class RecordingAssetLoader extends AssetLoader {
        private final List<String> requestedCardCodes = new ArrayList<>();

        @Override
        public Node loadCard(String cardCode, double width, double height) {
            requestedCardCodes.add(cardCode);
            return new StackPane(new Label(cardCode));
        }

        List<String> requestedCardCodes() {
            return requestedCardCodes;
        }
    }
}

