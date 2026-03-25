package com.seminairepoker.frontend.presentation.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class ActionBarView extends HBox {
    private static final double BASE_BUTTON_WIDTH = 140;

    public ActionBarView() {
        getStyleClass().add("action-bar");
        setAlignment(Pos.CENTER);
        setSpacing(12);
        setPadding(new Insets(8, 0, 0, 0));

        getChildren().addAll(
                buildActionButton("Fold", "action-danger"),
                buildActionButton("Check", "action-neutral"),
                buildActionButton("Call", "action-primary"),
                buildActionButton("Raise", "action-strong")
        );
    }

    private Button buildActionButton(String text, String cssClass) {
        Button button = new Button(text);
        button.getStyleClass().addAll("action-button", cssClass);
        button.setPrefWidth(BASE_BUTTON_WIDTH);
        return button;
    }

    public void setButtonScale(double scale) {
        double buttonWidth = BASE_BUTTON_WIDTH * scale;
        getChildren().forEach(node -> {
            if (node instanceof Button button) {
                button.setPrefWidth(buttonWidth);
            }
        });
    }
}

