package com.seminairepoker.frontend.presentation.view;

import com.seminairepoker.frontend.presentation.state.HomePageUiState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Objects;
import java.util.function.Consumer;

public class HomePageView extends StackPane {
    private final Button createTableButton;
    private final Button joinTableButton;
    private final JoinTableFormView joinTableFormView;

    public HomePageView(HomePageUiState state) {
        Objects.requireNonNull(state, "state must not be null");

        getStyleClass().add("home-screen");

        Label titleLabel = new Label(state.title());
        titleLabel.getStyleClass().add("home-title");

        Label subtitleLabel = new Label(state.subtitle());
        subtitleLabel.getStyleClass().add("home-subtitle");

        createTableButton = new Button(state.createTableLabel());
        createTableButton.getStyleClass().addAll("home-button", "home-button-primary");

        joinTableButton = new Button(state.joinTableLabel());
        joinTableButton.getStyleClass().addAll("home-button", "home-button-secondary");

        joinTableFormView = new JoinTableFormView(state.joinTableForm());

        joinTableButton.setOnAction(event -> joinTableFormView.setFormVisible(true));

        VBox panel = new VBox(18, titleLabel, subtitleLabel, createTableButton, joinTableButton, joinTableFormView);
        panel.getStyleClass().add("home-panel");
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(34));

        setAlignment(Pos.CENTER);
        getChildren().add(panel);
    }

    public void setOnCreateTableRequested(Runnable onCreateTableRequested) {
        createTableButton.setOnAction(event -> onCreateTableRequested.run());
    }

    public void setOnJoinTableRequested(Consumer<String> onJoinTableRequested) {
        joinTableFormView.setOnSubmit(onJoinTableRequested);
    }

    public void showJoinValidationMessage(String message) {
        joinTableFormView.setValidationMessage(message);
    }

    public Button getCreateTableButton() {
        return createTableButton;
    }

    public Button getJoinTableButton() {
        return joinTableButton;
    }

    public JoinTableFormView getJoinTableFormView() {
        return joinTableFormView;
    }
}

