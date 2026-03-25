package com.seminairepoker.frontend.presentation.view;

import com.seminairepoker.frontend.presentation.state.PlayerSeatUiState;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
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
        if (state.acting()) {
            getStyleClass().add("seat-acting");
        }
        setAlignment(Pos.CENTER);
        setSpacing(4);

        Label nameLabel = new Label(state.occupied() ? state.playerName() : "Empty");
        nameLabel.getStyleClass().add("seat-name");

        Label stackLabel = new Label(state.occupied() ? formatEuro(state.stack()) : "-");
        stackLabel.getStyleClass().add("seat-stack");

        Label roleLabel = new Label(state.dealer() ? "D" : "");
        roleLabel.getStyleClass().add("seat-role");

        getChildren().addAll(nameLabel, stackLabel, roleLabel);
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

