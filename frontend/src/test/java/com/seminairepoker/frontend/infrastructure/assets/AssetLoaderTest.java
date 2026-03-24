package com.seminairepoker.frontend.infrastructure.assets;

import com.seminairepoker.frontend.support.FxUiTestSupport;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssetLoaderTest extends FxUiTestSupport {

    @Test
    void shouldLoadImageView_whenCardAssetExists() throws Exception {
        // Arrange
        AssetLoader assetLoader = new AssetLoader();

        // Act
        Node node = onFxThread(() -> assetLoader.loadCard("ace_of_spades", 86, 124));

        // Assert
        ImageView imageView = assertInstanceOf(ImageView.class, node);
        assertAll(
                () -> assertEquals(86.0, imageView.getFitWidth()),
                () -> assertEquals(124.0, imageView.getFitHeight())
        );
    }

    @Test
    void shouldReturnFallbackCardLabel_whenCardAssetDoesNotExist() throws Exception {
        // Arrange
        AssetLoader assetLoader = new AssetLoader();

        // Act
        Node node = onFxThread(() -> assetLoader.loadCard("unknown card", 86, 124));

        // Assert
        StackPane fallback = assertInstanceOf(StackPane.class, node);
        Label fallbackLabel = (Label) fallback.getChildren().get(1);
        assertEquals("UNKNOWN CARD", fallbackLabel.getText());
    }

    @Test
    void shouldLoadTableAsImageView_whenTableAssetExists() throws Exception {
        // Arrange
        AssetLoader assetLoader = new AssetLoader();

        // Act
        Node node = onFxThread(() -> assetLoader.loadTable(980, 520));

        // Assert
        ImageView imageView = assertInstanceOf(ImageView.class, node);
        assertTrue(imageView.getFitWidth() > 0);
    }
}

