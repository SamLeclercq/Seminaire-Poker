package com.seminairepoker.frontend.presentation.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import java.util.Objects;

public class ActionBarView extends HBox {
    private static final double BASE_BUTTON_WIDTH = 140;
    private final Button readyButton;

    public ActionBarView() {
        this(() -> { });
    }

    public ActionBarView(Runnable onReadyRequested) {
        Runnable safeOnReadyRequested = Objects.requireNonNull(onReadyRequested, "onReadyRequested must not be null");
        getStyleClass().add("action-bar");
        setAlignment(Pos.CENTER);
        setSpacing(12);
        setPadding(new Insets(8, 0, 0, 0));

        readyButton = buildActionButton("Pret", "action-ready-pending");
        readyButton.setOnAction(event -> safeOnReadyRequested.run());

        getChildren().addAll(
                buildActionButton("Fold", "action-danger"),
                buildActionButton("Check", "action-neutral"),
                buildActionButton("Call", "action-primary"),
                buildActionButton("Raise", "action-strong"),
                readyButton
        );

        applyReadyState(false, false);
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

    public void applyReadyState(boolean waitingForReady, boolean localPlayerReady) {
        readyButton.getStyleClass().removeAll("action-ready-pending", "action-ready-active");
        if (localPlayerReady) {
            readyButton.getStyleClass().add("action-ready-active");
        } else {
            readyButton.getStyleClass().add("action-ready-pending");
        }
        readyButton.setDisable(!waitingForReady || localPlayerReady);
        readyButton.setVisible(waitingForReady);
        readyButton.setManaged(waitingForReady);
    }

    public Button readyButton() {
        return readyButton;
    }
}

