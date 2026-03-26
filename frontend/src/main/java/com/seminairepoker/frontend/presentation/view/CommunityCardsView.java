package com.seminairepoker.frontend.presentation.view;

import com.seminairepoker.frontend.infrastructure.assets.AssetLoader;
import com.seminairepoker.frontend.shared.cards.CardCodes;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class CommunityCardsView extends HBox {
    private static final int CARD_SLOTS = 5;

    public CommunityCardsView(List<String> cardCodes, AssetLoader assetLoader) {
        getStyleClass().add("community-cards");
        setSpacing(10);
        setAlignment(Pos.CENTER);

        for (int index = 0; index < CARD_SLOTS; index++) {
            String cardCode = index < cardCodes.size() ? cardCodes.get(index) : CardCodes.HIDDEN;
            Node cardNode = assetLoader.loadCard(cardCode, 86, 124);
            StackPane cardSlot = new StackPane(cardNode);
            cardSlot.getStyleClass().add("community-card-slot");
            if (index % 2 == 0) {
                cardSlot.setRotate(-1.2);
            } else {
                cardSlot.setRotate(1.2);
            }
            getChildren().add(cardSlot);
        }
    }

    public void setCardSize(double width, double height) {
        for (Node slotNode : getChildren()) {
            if (slotNode instanceof StackPane slot) {
                slot.setPrefSize(width, height);
                if (!slot.getChildren().isEmpty()) {
                    resizeCardNode(slot.getChildren().getFirst(), width, height);
                }
            }
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

