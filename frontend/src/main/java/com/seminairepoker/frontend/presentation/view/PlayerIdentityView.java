package com.seminairepoker.frontend.presentation.view;

import com.seminairepoker.frontend.presentation.state.PlayerIdentityUiState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Objects;
import java.util.function.Consumer;

public class PlayerIdentityView extends StackPane {
    private final TextField playerNameInput;
    private final Button connectButton;
    private final Label validationLabel;

    public PlayerIdentityView(PlayerIdentityUiState state) {
        Objects.requireNonNull(state, "state must not be null");

        getStyleClass().add("identity-screen");

        Label titleLabel = new Label(state.title());
        titleLabel.getStyleClass().add("identity-title");

        Label subtitleLabel = new Label(state.subtitle());
        subtitleLabel.getStyleClass().add("identity-subtitle");

        playerNameInput = new TextField();
        playerNameInput.setPromptText(state.playerNamePlaceholder());
        playerNameInput.setAlignment(Pos.CENTER);
        playerNameInput.getStyleClass().add("identity-input");

        connectButton = new Button(state.submitLabel());
        connectButton.getStyleClass().addAll("home-button", "home-button-primary", "identity-button");

        validationLabel = new Label(state.validationMessage());
        validationLabel.getStyleClass().add("identity-validation");

        VBox panel = new VBox(16, titleLabel, subtitleLabel, playerNameInput, connectButton, validationLabel);
        panel.getStyleClass().add("identity-panel");
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(28));
        panel.setMaxWidth(400);
        panel.setMaxHeight(Region.USE_PREF_SIZE);

        setAlignment(Pos.CENTER);
        getChildren().add(panel);

        showValidationMessage(state.validationMessage());
    }

    public void setOnConnectRequested(Consumer<String> onConnectRequested) {
        connectButton.setOnAction(event -> onConnectRequested.accept(playerNameInput.getText()));
        playerNameInput.setOnAction(event -> onConnectRequested.accept(playerNameInput.getText()));
    }

    public void showValidationMessage(String message) {
        String safeMessage = message == null ? "" : message;
        validationLabel.setText(safeMessage);
        boolean hasMessage = !safeMessage.isBlank();
        validationLabel.setManaged(hasMessage);
        validationLabel.setVisible(hasMessage);
    }

    public TextField getPlayerNameInput() {
        return playerNameInput;
    }

    public Button getConnectButton() {
        return connectButton;
    }

    public Label getValidationLabel() {
        return validationLabel;
    }
}

