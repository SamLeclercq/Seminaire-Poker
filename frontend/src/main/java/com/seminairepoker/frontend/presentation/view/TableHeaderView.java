package com.seminairepoker.frontend.presentation.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public class TableHeaderView extends HBox {

    public TableHeaderView(String tableCode, Runnable onReturnHomeRequested) {
        getStyleClass().add("table-header");
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(12);

        Label tableCodeLabel = new Label("Table " + tableCode);
        tableCodeLabel.getStyleClass().add("table-id-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Button backHomeButton = new Button("Retour accueil");
        backHomeButton.getStyleClass().addAll("table-leave-button", "table-return-home-button");
        backHomeButton.setOnAction(event -> onReturnHomeRequested.run());

        getChildren().addAll(tableCodeLabel, spacer, backHomeButton);
    }
}
