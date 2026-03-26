package com.seminairepoker.frontend.presentation.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.text.NumberFormat;
import java.util.Locale;

public class PotView extends VBox {
    private static final NumberFormat EURO_FORMAT = NumberFormat.getCurrencyInstance(Locale.FRANCE);

    static {
        EURO_FORMAT.setMaximumFractionDigits(0);
        EURO_FORMAT.setMinimumFractionDigits(0);
    }

    public PotView(int potValue, String roundLabel) {
        getStyleClass().add("pot-box");
        setAlignment(Pos.CENTER);
        setSpacing(4);

        Label round = new Label("Round: " + roundLabel);
        round.getStyleClass().add("round-label");

        Label pot = new Label("Pot: " + EURO_FORMAT.format(potValue));
        pot.getStyleClass().add("pot-label");

        getChildren().addAll(round, pot);
    }
}

