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

class CommunityCardsViewTest extends FxUiTestSupport {

    @Test
    void shouldRenderExactlyFiveSlots_whenCardsAreMissing() throws Exception {
        // Arrange
        RecordingAssetLoader assetLoader = new RecordingAssetLoader();

        // Act
        List<String> requestedCodes = onFxThread(() -> {
            new CommunityCardsView(List.of("ace_of_spades", "king_of_spades"), assetLoader);
            return List.copyOf(assetLoader.requestedCardCodes());
        });

        // Assert
        assertEquals(
                List.of("ace_of_spades", "king_of_spades", "card_face_down", "card_face_down", "card_face_down"),
                requestedCodes
        );
    }

    @Test
    void shouldRenderFiveFaceDownCards_whenNoCommunityCardIsProvided() throws Exception {
        // Arrange
        RecordingAssetLoader assetLoader = new RecordingAssetLoader();

        // Act
        List<String> requestedCodes = onFxThread(() -> {
            new CommunityCardsView(List.of(), assetLoader);
            return List.copyOf(assetLoader.requestedCardCodes());
        });

        // Assert
        assertEquals(
                List.of("card_face_down", "card_face_down", "card_face_down", "card_face_down", "card_face_down"),
                requestedCodes
        );
    }

    @Test
    void shouldResizeFallbackCards_whenSetCardSize() throws Exception {
        // Arrange
        RecordingAssetLoader assetLoader = new RecordingAssetLoader();
        CommunityCardsView communityCardsView = onFxThread(() -> new CommunityCardsView(List.of(), assetLoader));

        // Act
        List<Double> widths = onFxThread(() -> {
            communityCardsView.setCardSize(92, 130);
            return communityCardsView.getChildren()
                    .stream()
                    .map(node -> ((StackPane) node).getPrefWidth())
                    .toList();
        });

        // Assert
        assertTrue(widths.stream().allMatch(width -> width.equals(92.0)));
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

