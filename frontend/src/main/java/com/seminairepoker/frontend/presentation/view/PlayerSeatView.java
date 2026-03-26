package com.seminairepoker.frontend.presentation.view;

import com.seminairepoker.frontend.infrastructure.assets.AssetLoader;
import com.seminairepoker.frontend.presentation.state.PlayerSeatUiState;
import com.seminairepoker.frontend.shared.cards.CardCodes;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PlayerSeatView extends VBox {
    private static final NumberFormat EURO_FORMAT = NumberFormat.getCurrencyInstance(Locale.FRANCE);
    private static final double MIN_CARD_WIDTH = 24;
    private static final double MAX_CARD_WIDTH = 46;
    private static final double CARD_RATIO = 1.45;

    private final List<StackPane> cardSlots = new java.util.ArrayList<>();
    private final Label nameLabel;
    private final Label stackLabel;
    private HBox cardsRow;

    static {
        EURO_FORMAT.setMaximumFractionDigits(0);
        EURO_FORMAT.setMinimumFractionDigits(0);
    }

    public PlayerSeatView(PlayerSeatUiState state) {
        this(state, new AssetLoader());
    }

    public PlayerSeatView(PlayerSeatUiState state, AssetLoader assetLoader) {
        PlayerSeatUiState safeState = Objects.requireNonNull(state, "state must not be null");
        AssetLoader safeAssetLoader = Objects.requireNonNull(assetLoader, "assetLoader must not be null");

        getStyleClass().add("seat");
        getStyleClass().add(safeState.occupied() ? "seat-occupied" : "seat-empty");
        if (safeState.acting()) {
            getStyleClass().add("seat-acting");
        }
        if (safeState.dealer()) {
            getStyleClass().add("seat-dealer");
        }
        if (safeState.currentPlayer()) {
            getStyleClass().add("seat-current-player");
        }
        setAlignment(Pos.CENTER);
        setSpacing(5);

        nameLabel = new Label(safeState.occupied() ? safeState.playerName() : "Empty");
        nameLabel.getStyleClass().add("seat-name");
        nameLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
        nameLabel.setWrapText(false);

        stackLabel = new Label(safeState.occupied() ? formatEuro(safeState.stack()) : "-");
        stackLabel.getStyleClass().add("seat-stack");

        Label roleLabel = new Label(safeState.dealer() ? "D" : "");
        roleLabel.getStyleClass().add("seat-role");

        Label readyIndicator = new Label();
        readyIndicator.getStyleClass().add("seat-ready-indicator");
        if (safeState.occupied()) {
            readyIndicator.getStyleClass().add(safeState.ready() ? "seat-ready" : "seat-not-ready");
        } else {
            readyIndicator.setVisible(false);
            readyIndicator.setManaged(false);
        }

        HBox statusRow = new HBox(8, roleLabel, readyIndicator);
        statusRow.getStyleClass().add("seat-status-row");
        statusRow.setAlignment(Pos.CENTER);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        HBox headerRow = new HBox(8, nameLabel, headerSpacer, statusRow);
        headerRow.getStyleClass().add("seat-header-row");
        headerRow.setAlignment(Pos.CENTER_LEFT);
        headerRow.setMaxWidth(Double.MAX_VALUE);

        cardsRow = createCardsRow(safeState, safeAssetLoader);

        getChildren().addAll(headerRow, stackLabel, cardsRow);
        setSeatSize(130, 84);
    }

    private HBox createCardsRow(PlayerSeatUiState state, AssetLoader assetLoader) {
        HBox cardsRow = new HBox(3);
        cardsRow.getStyleClass().add("seat-cards");
        cardsRow.setAlignment(Pos.CENTER);

        for (String cardCode : resolveCardsForDisplay(state)) {
            Node cardNode = assetLoader.loadCard(cardCode, 20, 30);
            StackPane slot = new StackPane(cardNode);
            slot.getStyleClass().add("seat-card-slot");
            cardSlots.add(slot);
            cardsRow.getChildren().add(slot);
        }
        return cardsRow;
    }

    private List<String> resolveCardsForDisplay(PlayerSeatUiState state) {
        if (!state.occupied()) {
            return List.of();
        }

        if (!state.currentPlayer()) {
            return List.of(CardCodes.HIDDEN, CardCodes.HIDDEN);
        }

        List<String> localCards = state.cards();
        if (localCards.size() >= 2) {
            return List.of(localCards.get(0), localCards.get(1));
        }
        if (localCards.size() == 1) {
            return List.of(localCards.getFirst(), CardCodes.HIDDEN);
        }
        return List.of(CardCodes.HIDDEN, CardCodes.HIDDEN);
    }

    public void setSeatSize(double width, double height) {
        setPrefSize(width, height);
        setMinSize(width, height);
        setMaxSize(width, height);

        nameLabel.setMaxWidth(width * 0.58);

        applyResponsiveCardSize(width);
    }

    private void applyResponsiveCardSize(double seatWidth) {
        if (cardsRow == null || cardSlots.isEmpty()) {
            return;
        }

        double cardWidth = clamp(seatWidth * 0.26, MIN_CARD_WIDTH, MAX_CARD_WIDTH);
        double cardHeight = cardWidth * CARD_RATIO;
        cardsRow.setSpacing(Math.max(5, cardWidth * 0.2));

        for (StackPane slot : cardSlots) {
            slot.setPrefSize(cardWidth, cardHeight);
            if (slot.getChildren().isEmpty()) {
                continue;
            }

            Node node = slot.getChildren().getFirst();
            if (node instanceof ImageView imageView) {
                imageView.setFitWidth(cardWidth);
                imageView.setFitHeight(cardHeight);
                continue;
            }

            if (node instanceof StackPane fallback) {
                fallback.setPrefSize(cardWidth, cardHeight);
                for (Node fallbackChild : fallback.getChildren()) {
                    if (fallbackChild instanceof Rectangle rectangle) {
                        rectangle.setWidth(cardWidth);
                        rectangle.setHeight(cardHeight);
                    }
                }
            }
        }
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private String formatEuro(int amount) {
        return EURO_FORMAT.format(amount);
    }
}

