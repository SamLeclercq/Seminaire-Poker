package com.seminairepoker.frontend.presentation.view;

import com.seminairepoker.frontend.infrastructure.assets.AssetLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.Objects;

public class PlayerHandView extends HBox {
    private static final int HAND_CARD_COUNT = 2;
    private static final String EMPTY_CARD_CODE = "";

    public PlayerHandView(List<String> cards, AssetLoader assetLoader) {
        List<String> safeCards = Objects.requireNonNull(cards, "cards must not be null");
        AssetLoader safeAssetLoader = Objects.requireNonNull(assetLoader, "assetLoader must not be null");

        getStyleClass().add("player-hand");
        setAlignment(Pos.CENTER);
        setSpacing(14);

        for (int index = 0; index < HAND_CARD_COUNT; index++) {
            getChildren().add(createCardNode(safeCards, index, safeAssetLoader));
        }
    }

    public void setCardSize(double width, double height) {
        for (Node cardNode : getChildren()) {
            resizeCardNode(cardNode, width, height);
        }
    }

    private void resizeCardNode(Node cardNode, double width, double height) {
        if (cardNode instanceof ImageView imageView) {
            setNodeSize(imageView, width, height);
            return;
        }
        if (cardNode instanceof StackPane fallback) {
            resizeFallbackCard(fallback, width, height);
        }
    }

    private static Node createCardNode(List<String> cards, int index, AssetLoader assetLoader) {
        String cardCode = index < cards.size() ? cards.get(index) : EMPTY_CARD_CODE;
        return assetLoader.loadCard(cardCode, 110, 160);
    }

    private static void resizeFallbackCard(StackPane fallback, double width, double height) {
        fallback.setPrefSize(width, height);
        for (Node fallbackChild : fallback.getChildren()) {
            if (fallbackChild instanceof Rectangle rectangle) {
                setNodeSize(rectangle, width, height);
            }
        }
    }

    private static void setNodeSize(ImageView imageView, double width, double height) {
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
    }

    private static void setNodeSize(Rectangle rectangle, double width, double height) {
        rectangle.setWidth(width);
        rectangle.setHeight(height);
    }
}

