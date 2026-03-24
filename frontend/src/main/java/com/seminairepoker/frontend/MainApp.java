package com.seminairepoker.frontend;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {
    static final String WINDOW_TITLE = "Seminaire Poker - Frontend";

    @Override
    public void start(Stage stage) {
        Label title = new Label("Seminaire Poker");
        title.getStyleClass().add("title");

        Label subtitle = new Label("Base visuelle JavaFX prete pour ton projet.");
        Button actionButton = new Button("Demarrer le design");
        actionButton.setOnAction(event -> subtitle.setText("UI chargee. Tu peux construire les ecrans."));

        VBox root = new VBox(12, title, subtitle, actionButton);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 900, 560);
        scene.getStylesheets().add(MainApp.class.getResource("/com/seminairepoker/frontend/application.css").toExternalForm());

        stage.setTitle(WINDOW_TITLE);
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
