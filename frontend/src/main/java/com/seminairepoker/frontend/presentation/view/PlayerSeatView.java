package com.seminairepoker.frontend.presentation.view;

import com.seminairepoker.frontend.presentation.state.PlayerSeatUiState;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.text.NumberFormat;
import java.util.Locale;

public class PlayerSeatView extends VBox {
    private static final NumberFormat EURO_FORMAT = NumberFormat.getCurrencyInstance(Locale.FRANCE);

    static {
        EURO_FORMAT.setMaximumFractionDigits(0);
        EURO_FORMAT.setMinimumFractionDigits(0);
    }

    public PlayerSeatView(PlayerSeatUiState state) {
        getStyleClass().add("seat");
        getStyleClass().add(state.occupied() ? "seat-occupied" : "seat-empty");
        if (state.acting()) {
            getStyleClass().add("seat-acting");
        }
        if (state.dealer()) {
            getStyleClass().add("seat-dealer");
        }
        setAlignment(Pos.CENTER);
        setSpacing(4);

        Label nameLabel = new Label(state.occupied() ? state.playerName() : "Empty");
        nameLabel.getStyleClass().add("seat-name");

        Label stackLabel = new Label(state.occupied() ? formatEuro(state.stack()) : "-");
        stackLabel.getStyleClass().add("seat-stack");

        Label roleLabel = new Label(state.dealer() ? "D" : "");
        roleLabel.getStyleClass().add("seat-role");

        Label readyIndicator = new Label();
        readyIndicator.getStyleClass().add("seat-ready-indicator");
        if (state.occupied()) {
            readyIndicator.getStyleClass().add(state.ready() ? "seat-ready" : "seat-not-ready");
        } else {
            readyIndicator.setVisible(false);
            readyIndicator.setManaged(false);
        }

        HBox statusRow = new HBox(6, roleLabel, readyIndicator);
        statusRow.setAlignment(Pos.CENTER);

        getChildren().addAll(nameLabel, stackLabel, statusRow);
        setSeatSize(130, 84);
    }

    public void setSeatSize(double width, double height) {
        setPrefSize(width, height);
        setMinSize(width, height);
        setMaxSize(width, height);
    }

    private String formatEuro(int amount) {
        return EURO_FORMAT.format(amount);
    }
}

