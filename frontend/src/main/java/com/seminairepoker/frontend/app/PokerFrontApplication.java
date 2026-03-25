package com.seminairepoker.frontend.app;

import com.seminairepoker.frontend.application.port.CreateTablePort;
import com.seminairepoker.frontend.application.port.JoinTablePort;
import com.seminairepoker.frontend.application.port.TableStateProvider;
import com.seminairepoker.frontend.application.service.CreateTableService;
import com.seminairepoker.frontend.application.service.JoinTableService;
import com.seminairepoker.frontend.application.service.LoadTableStateService;
import com.seminairepoker.frontend.application.service.TableCodeValidator;
import com.seminairepoker.frontend.infrastructure.assets.AssetLoader;
import com.seminairepoker.frontend.infrastructure.provider.FallbackTableStateProvider;
import com.seminairepoker.frontend.infrastructure.provider.InMemoryCreateTableProvider;
import com.seminairepoker.frontend.infrastructure.provider.InMemoryJoinTableProvider;
import com.seminairepoker.frontend.infrastructure.provider.InMemoryTableStateProvider;
import com.seminairepoker.frontend.infrastructure.provider.WebSocketTableStateProvider;
import com.seminairepoker.frontend.presentation.state.HomePageUiState;
import com.seminairepoker.frontend.presentation.state.JoinTableFormUiState;
import com.seminairepoker.frontend.presentation.state.TableUiState;
import com.seminairepoker.frontend.presentation.view.HomePageView;
import com.seminairepoker.frontend.presentation.view.PokerTableView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.security.SecureRandom;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PokerFrontApplication extends Application {
    public static final String WINDOW_TITLE = "Seminaire Poker - Table";
    private static final String TABLE_CODE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Override
    public void start(Stage stage) {
        TableStateProvider tableStateProvider = createTableStateProvider();
        LoadTableStateService loadTableStateService = new LoadTableStateService(tableStateProvider);
        AssetLoader assetLoader = new AssetLoader();
        TableCodeValidator tableCodeValidator = new TableCodeValidator();

        Set<String> knownTableCodes = ConcurrentHashMap.newKeySet();
        CreateTableService createTableService = new CreateTableService(
                createCreateTablePort(knownTableCodes),
                tableCodeValidator
        );
        JoinTableService joinTableService = new JoinTableService(
                createJoinTablePort(knownTableCodes),
                tableCodeValidator
        );

        Scene scene = new Scene(new StackPane(), 1280, 820);
        applyStylesheets(scene);
        showHome(scene, createTableService, joinTableService, tableCodeValidator, loadTableStateService, assetLoader);

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

    static CreateTablePort createCreateTablePort(Set<String> knownTableCodes) {
        SecureRandom random = new SecureRandom();
        return new InMemoryCreateTableProvider(
                () -> generateTableCode(random),
                knownTableCodes::add
        );
    }

    static JoinTablePort createJoinTablePort(Set<String> knownTableCodes) {
        return new InMemoryJoinTableProvider(code -> knownTableCodes.contains(code) || code.matches("[A-Z0-9]{5}"));
    }

    private static String generateTableCode(SecureRandom random) {
        StringBuilder builder = new StringBuilder(5);
        for (int index = 0; index < 5; index++) {
            int randomIndex = random.nextInt(TABLE_CODE_ALPHABET.length());
            builder.append(TABLE_CODE_ALPHABET.charAt(randomIndex));
        }
        return builder.toString();
    }

    private void showHome(
            Scene scene,
            CreateTableService createTableService,
            JoinTableService joinTableService,
            TableCodeValidator tableCodeValidator,
            LoadTableStateService loadTableStateService,
            AssetLoader assetLoader
    ) {
        HomePageView homePageView = new HomePageView(createHomePageUiState());
        scene.setRoot(homePageView);

        homePageView.setOnCreateTableRequested(() -> {
            String tableCode = createTableService.createTable();
            navigateToGame(scene, tableCode, loadTableStateService, assetLoader, createTableService, joinTableService, tableCodeValidator);
        });

        homePageView.setOnJoinTableRequested(tableCode -> {
            if (!tableCodeValidator.isValid(tableCode)) {
                homePageView.showJoinValidationMessage("Le code doit contenir exactement 5 caracteres.");
                return;
            }
            boolean joined = joinTableService.joinTable(tableCode);
            if (!joined) {
                homePageView.showJoinValidationMessage("Impossible de rejoindre cette table.");
                return;
            }
            navigateToGame(
                    scene,
                    tableCodeValidator.normalize(tableCode),
                    loadTableStateService,
                    assetLoader,
                    createTableService,
                    joinTableService,
                    tableCodeValidator
            );
        });
    }

    private void navigateToGame(
            Scene scene,
            String tableCode,
            LoadTableStateService loadTableStateService,
            AssetLoader assetLoader,
            CreateTableService createTableService,
            JoinTableService joinTableService,
            TableCodeValidator tableCodeValidator
    ) {
        TableUiState initialState = loadTableStateService.loadInitialState().withTableCode(tableCode);
        scene.setRoot(new PokerTableView(
                initialState,
                assetLoader,
                () -> showHome(scene, createTableService, joinTableService, tableCodeValidator, loadTableStateService, assetLoader)
        ));
    }

    private HomePageUiState createHomePageUiState() {
        return new HomePageUiState(
                "Seminaire Poker",
                "Create or join a table to start playing.",
                "Creer une table",
                "Rejoindre une table",
                new JoinTableFormUiState(false, "Code de table (5 caracteres)", "Valider", "")
        );
    }

    private void applyStylesheets(Scene scene) {
        List<String> stylesheets = List.of("/css/table.css", "/css/components.css", "/css/home.css");
        for (String stylesheet : stylesheets) {
            var resource = PokerFrontApplication.class.getResource(stylesheet);
            if (resource != null) {
                scene.getStylesheets().add(resource.toExternalForm());
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
