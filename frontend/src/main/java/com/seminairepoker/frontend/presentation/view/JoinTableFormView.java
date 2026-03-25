package com.seminairepoker.frontend.presentation.view;

import com.seminairepoker.frontend.presentation.state.JoinTableFormUiState;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Objects;
import java.util.function.Consumer;

public class JoinTableFormView extends VBox {
    private final TextField tableCodeInput;
    private final Button submitButton;
    private final Label validationLabel;

    public JoinTableFormView(JoinTableFormUiState state) {
        Objects.requireNonNull(state, "state must not be null");

        getStyleClass().add("join-form");
        setSpacing(10);
        setAlignment(Pos.CENTER);

        tableCodeInput = new TextField();
        tableCodeInput.setPromptText(state.placeholder());
        tableCodeInput.getStyleClass().add("join-input");

        submitButton = new Button(state.submitLabel());
        submitButton.getStyleClass().addAll("home-button", "home-button-secondary");

        HBox row = new HBox(10, tableCodeInput, submitButton);
        row.setAlignment(Pos.CENTER);

        validationLabel = new Label(state.validationMessage());
        validationLabel.getStyleClass().add("join-validation");

        getChildren().addAll(row, validationLabel);

        setFormVisible(state.visible());
        setValidationMessage(state.validationMessage());
    }

    public void setOnSubmit(Consumer<String> onSubmit) {
        submitButton.setOnAction(event -> onSubmit.accept(tableCodeInput.getText()));
    }

    public void setValidationMessage(String message) {
        String safeMessage = message == null ? "" : message;
        validationLabel.setText(safeMessage);
        boolean hasMessage = !safeMessage.isBlank();
        validationLabel.setManaged(hasMessage);
        validationLabel.setVisible(hasMessage);
    }

    public void setFormVisible(boolean visible) {
        setVisible(visible);
        setManaged(visible);
    }

    public boolean isFormVisible() {
        return isVisible();
    }

    public String getTableCode() {
        return tableCodeInput.getText();
    }
}

