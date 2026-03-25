package com.seminairepoker.frontend.app;

import com.seminairepoker.frontend.application.port.TableStateProvider;
import com.seminairepoker.frontend.application.service.LoadTableStateService;
import com.seminairepoker.frontend.infrastructure.assets.AssetLoader;
import com.seminairepoker.frontend.infrastructure.provider.FallbackTableStateProvider;
import com.seminairepoker.frontend.infrastructure.provider.InMemoryTableStateProvider;
import com.seminairepoker.frontend.infrastructure.provider.WebSocketTableStateProvider;
import com.seminairepoker.frontend.presentation.state.TableUiState;
import com.seminairepoker.frontend.presentation.view.PokerTableView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

public class PokerFrontApplication extends Application {
    public static final String WINDOW_TITLE = "Seminaire Poker - Table";

    @Override
    public void start(Stage stage) {
        TableStateProvider tableStateProvider = createTableStateProvider();
        LoadTableStateService loadTableStateService = new LoadTableStateService(tableStateProvider);
        TableUiState initialState = loadTableStateService.loadInitialState();
        AssetLoader assetLoader = new AssetLoader();

        PokerTableView root = new PokerTableView(initialState, assetLoader);
        Scene scene = new Scene(root, 1280, 820);

        List<String> stylesheets = List.of("/css/table.css", "/css/components.css");
        for (String stylesheet : stylesheets) {
            var resource = PokerFrontApplication.class.getResource(stylesheet);
            if (resource != null) {
                scene.getStylesheets().add(resource.toExternalForm());
            }
        }

        stage.setTitle(WINDOW_TITLE);
        stage.setMinWidth(1080);
        stage.setMinHeight(720);
        stage.setScene(scene);
        stage.show();
    }

    static TableStateProvider createTableStateProvider() {
        TableStateProvider backendProvider = new WebSocketTableStateProvider();
        TableStateProvider fallbackProvider = new InMemoryTableStateProvider();
        return new FallbackTableStateProvider(backendProvider, fallbackProvider);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

