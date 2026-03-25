package com.seminairepoker.frontend.presentation.view;

import com.seminairepoker.frontend.infrastructure.assets.AssetLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class PlayerHandView extends HBox {
    public PlayerHandView(List<String> cards, AssetLoader assetLoader) {
        getStyleClass().add("player-hand");
        setAlignment(Pos.CENTER);
        setSpacing(14);

        for (int index = 0; index < 2; index++) {
            String cardCode = index < cards.size() ? cards.get(index) : "";
            Node cardNode = assetLoader.loadCard(cardCode, 110, 160);
            getChildren().add(cardNode);
        }
    }

    public void setCardSize(double width, double height) {
        for (Node cardNode : getChildren()) {
            resizeCardNode(cardNode, width, height);
        }
    }

    private void resizeCardNode(Node cardNode, double width, double height) {
        if (cardNode instanceof ImageView imageView) {
            imageView.setFitWidth(width);
            imageView.setFitHeight(height);
            return;
        }
        if (cardNode instanceof StackPane fallback) {
            fallback.setPrefSize(width, height);
            for (Node fallbackChild : fallback.getChildren()) {
                if (fallbackChild instanceof Rectangle rectangle) {
                    rectangle.setWidth(width);
                    rectangle.setHeight(height);
                }
            }
        }
    }
}

