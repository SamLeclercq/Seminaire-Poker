package com.seminairepoker.frontend.app;

import com.seminairepoker.frontend.application.port.TableStateProvider;
import com.seminairepoker.frontend.application.service.ConnectPlayerService;
import com.seminairepoker.frontend.application.service.CreateTableService;
import com.seminairepoker.frontend.application.service.JoinTableService;
import com.seminairepoker.frontend.application.service.LoadTableStateService;
import com.seminairepoker.frontend.application.service.PlayerNameValidator;
import com.seminairepoker.frontend.application.service.TableCodeValidator;
import com.seminairepoker.frontend.infrastructure.assets.AssetLoader;
import com.seminairepoker.frontend.infrastructure.provider.BackendWebSocketSession;
import com.seminairepoker.frontend.infrastructure.provider.JavaNetWebSocketSessionClient;
import com.seminairepoker.frontend.infrastructure.provider.WebSocketCreateTableProvider;
import com.seminairepoker.frontend.infrastructure.provider.WebSocketJoinTableProvider;
import com.seminairepoker.frontend.infrastructure.provider.WebSocketPlayerConnectionProvider;
import com.seminairepoker.frontend.infrastructure.provider.WebSocketTableStateProvider;
import com.seminairepoker.frontend.presentation.state.HomePageUiState;
import com.seminairepoker.frontend.presentation.state.JoinTableFormUiState;
import com.seminairepoker.frontend.presentation.state.PlayerIdentityUiState;
import com.seminairepoker.frontend.presentation.view.HomePageView;
import com.seminairepoker.frontend.presentation.view.PlayerIdentityView;
import com.seminairepoker.frontend.presentation.view.PokerTableView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PokerFrontApplication extends Application {
    public static final String WINDOW_TITLE = "Seminaire Poker - Table";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(2);
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "8765";

    @Override
    public void start(Stage stage) {
        BackendWebSocketSession backendSession = createBackendSession();

        TableStateProvider tableStateProvider = new WebSocketTableStateProvider(backendSession);
        LoadTableStateService loadTableStateService = new LoadTableStateService(tableStateProvider);
        AssetLoader assetLoader = new AssetLoader();
        TableCodeValidator tableCodeValidator = new TableCodeValidator();
        CreateTableService createTableService = new CreateTableService(
                new WebSocketCreateTableProvider(backendSession),
                tableCodeValidator
        );
        JoinTableService joinTableService = new JoinTableService(
                new WebSocketJoinTableProvider(backendSession),
                tableCodeValidator
        );

        ConnectPlayerService connectPlayerService = new ConnectPlayerService(
                new WebSocketPlayerConnectionProvider(backendSession),
                new PlayerNameValidator()
        );

        Scene scene = new Scene(new StackPane(), 1280, 820);
        applyStylesheets(scene);
        showIdentityPage(
                scene,
                connectPlayerService,
                createTableService,
                joinTableService,
                tableCodeValidator,
                loadTableStateService,
                assetLoader,
                null
        );

        stage.setTitle(WINDOW_TITLE);
        stage.setMinWidth(1080);
        stage.setMinHeight(720);
        stage.setScene(scene);
        stage.show();
    }

    static BackendWebSocketSession createBackendSession() {
        return new BackendWebSocketSession(resolveEndpointUri(), DEFAULT_TIMEOUT, new JavaNetWebSocketSessionClient());
    }

    private static URI resolveEndpointUri() {
        String wsUrl = System.getenv("POKER_WS_URL");
        if (wsUrl != null && !wsUrl.isBlank()) {
            return URI.create(wsUrl);
        }

        String host = System.getenv().getOrDefault("POKER_WS_HOST", DEFAULT_HOST);
        String port = System.getenv().getOrDefault("POKER_WS_PORT", DEFAULT_PORT);
        return URI.create("ws://" + host + ":" + port);
    }

    private void showIdentityPage(
            Scene scene,
            ConnectPlayerService connectPlayerService,
            CreateTableService createTableService,
            JoinTableService joinTableService,
            TableCodeValidator tableCodeValidator,
            LoadTableStateService loadTableStateService,
            AssetLoader assetLoader,
            String validationMessage
    ) {
        PlayerIdentityView identityView = new PlayerIdentityView(createPlayerIdentityUiState());
        scene.setRoot(identityView);
        if (validationMessage != null && !validationMessage.isBlank()) {
            identityView.showValidationMessage(validationMessage);
        }

        identityView.setOnConnectRequested(playerName -> {
            boolean isConnected;
            try {
                isConnected = connectPlayerService.connectPlayer(playerName);
            } catch (RuntimeException exception) {
                String causeMessage = exception.getCause() != null ? exception.getCause().getMessage() : exception.getMessage();
                if (causeMessage == null || causeMessage.isBlank()) {
                    identityView.showValidationMessage("Impossible de contacter le serveur.");
                } else {
                    identityView.showValidationMessage("Impossible de contacter le serveur: " + causeMessage);
                }
                return;
            }

            if (!isConnected) {
                identityView.showValidationMessage("Le prenom est requis.");
                return;
            }

            showHomePage(
                    scene,
                    createTableService,
                    joinTableService,
                    tableCodeValidator,
                    loadTableStateService,
                    assetLoader,
                    connectPlayerService,
                    null
            );
        });
    }

    private void showHomePage(
            Scene scene,
            CreateTableService createTableService,
            JoinTableService joinTableService,
            TableCodeValidator tableCodeValidator,
            LoadTableStateService loadTableStateService,
            AssetLoader assetLoader,
            ConnectPlayerService connectPlayerService,
            String joinValidationMessage
    ) {
        HomePageView homePageView = new HomePageView(createHomePageUiState());
        scene.setRoot(homePageView);
        if (joinValidationMessage != null && !joinValidationMessage.isBlank()) {
            homePageView.showJoinValidationMessage(joinValidationMessage);
        }

        homePageView.setOnCreateTableRequested(() -> {
            String tableCode = createTableService.createTable();
            navigateToRoom(
                    scene,
                    tableCode,
                    loadTableStateService,
                    assetLoader,
                    connectPlayerService,
                    createTableService,
                    joinTableService,
                    tableCodeValidator
            );
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

            navigateToRoom(
                    scene,
                    tableCodeValidator.normalize(tableCode),
                    loadTableStateService,
                    assetLoader,
                    connectPlayerService,
                    createTableService,
                    joinTableService,
                    tableCodeValidator
            );
        });
    }

    private void navigateToRoom(
            Scene scene,
            String tableCode,
            LoadTableStateService loadTableStateService,
            AssetLoader assetLoader,
            ConnectPlayerService connectPlayerService,
            CreateTableService createTableService,
            JoinTableService joinTableService,
            TableCodeValidator tableCodeValidator
    ) {
        scene.setRoot(createLoadingView());

        CompletableFuture
                .supplyAsync(() -> TableUiStateMapper
                        .toUiState(loadTableStateService.loadInitialState())
                        .withTableCode(tableCode))
                .whenComplete((initialState, throwable) -> Platform.runLater(() -> {
                    if (throwable != null) {
                        showHomePage(
                                scene,
                                createTableService,
                                joinTableService,
                                tableCodeValidator,
                                loadTableStateService,
                                assetLoader,
                                connectPlayerService,
                                "Impossible de charger l'etat de la table."
                        );
                        return;
                    }

                    Runnable returnHomeAction = createReturnHomeAction(
                            this::resetLocalSessionState,
                            () -> showIdentityPage(
                                    scene,
                                    connectPlayerService,
                                    createTableService,
                                    joinTableService,
                                    tableCodeValidator,
                                    loadTableStateService,
                                    assetLoader,
                                    null
                            )
                    );

                    scene.setRoot(new PokerTableView(
                            initialState,
                            assetLoader,
                            returnHomeAction
                    ));
                }));
    }

    static Runnable createReturnHomeAction(Runnable resetLocalUiState, Runnable showHomePageAction) {
        return () -> {
            resetLocalUiState.run();
            showHomePageAction.run();
        };
    }

    private void resetLocalSessionState() {
        // Reserved for local-only cleanup when leaving the table screen.
    }

    private StackPane createLoadingView() {
        Label loadingLabel = new Label("Chargement de la table...");
        loadingLabel.getStyleClass().add("home-subtitle");

        StackPane loadingRoot = new StackPane(loadingLabel);
        loadingRoot.getStyleClass().add("home-screen");
        return loadingRoot;
    }

    private PlayerIdentityUiState createPlayerIdentityUiState() {
        return new PlayerIdentityUiState(
                "Seminaire Poker",
                "Entrez votre prenom pour rejoindre la table.",
                "Prenom",
                "Se connecter",
                ""
        );
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
