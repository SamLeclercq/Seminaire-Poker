package com.seminairepoker.frontend.app;

import com.seminairepoker.frontend.application.port.CreateTablePort;
import com.seminairepoker.frontend.application.port.JoinTablePort;
import com.seminairepoker.frontend.application.port.TableStateProvider;
import com.seminairepoker.frontend.application.service.ConnectPlayerService;
import com.seminairepoker.frontend.application.service.CreateTableService;
import com.seminairepoker.frontend.application.service.DisconnectPlayerService;
import com.seminairepoker.frontend.application.service.JoinTableService;
import com.seminairepoker.frontend.application.service.LoadTableStateService;
import com.seminairepoker.frontend.application.service.PlayerNameValidator;
import com.seminairepoker.frontend.application.service.TableCodeValidator;
import com.seminairepoker.frontend.infrastructure.assets.AssetLoader;
import com.seminairepoker.frontend.infrastructure.provider.FallbackPlayerConnectionProvider;
import com.seminairepoker.frontend.infrastructure.provider.FallbackTableStateProvider;
import com.seminairepoker.frontend.infrastructure.provider.InMemoryCreateTableProvider;
import com.seminairepoker.frontend.infrastructure.provider.InMemoryJoinTableProvider;
import com.seminairepoker.frontend.infrastructure.provider.InMemoryPlayerConnectionProvider;
import com.seminairepoker.frontend.infrastructure.provider.InMemoryTableStateProvider;
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

import java.security.SecureRandom;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
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

        InMemoryPlayerConnectionProvider fallbackConnectionProvider = new InMemoryPlayerConnectionProvider();
        FallbackPlayerConnectionProvider playerConnectionProvider = createPlayerConnectionProvider(fallbackConnectionProvider);
        ConnectPlayerService connectPlayerService = new ConnectPlayerService(playerConnectionProvider, new PlayerNameValidator());
        DisconnectPlayerService disconnectPlayerService = new DisconnectPlayerService(playerConnectionProvider);

        Scene scene = new Scene(new StackPane(), 1280, 820);
        applyStylesheets(scene);
        showIdentityPage(
                scene,
                connectPlayerService,
                disconnectPlayerService,
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

    static TableStateProvider createTableStateProvider() {
        TableStateProvider backendProvider = new WebSocketTableStateProvider();
        TableStateProvider fallbackProvider = new InMemoryTableStateProvider();
        return new FallbackTableStateProvider(backendProvider, fallbackProvider);
    }

    static FallbackPlayerConnectionProvider createPlayerConnectionProvider(InMemoryPlayerConnectionProvider fallbackProvider) {
        WebSocketPlayerConnectionProvider backendProvider = new WebSocketPlayerConnectionProvider();
        return new FallbackPlayerConnectionProvider(backendProvider, backendProvider, fallbackProvider, fallbackProvider);
    }

    static CreateTablePort createCreateTablePort(Set<String> knownTableCodes) {
        SecureRandom random = new SecureRandom();
        return new InMemoryCreateTableProvider(
                () -> generateTableCode(random),
                knownTableCodes::add
        );
    }

    static JoinTablePort createJoinTablePort(Set<String> knownTableCodes) {
        return new InMemoryJoinTableProvider(knownTableCodes::contains);
    }

    private static String generateTableCode(SecureRandom random) {
        StringBuilder builder = new StringBuilder(5);
        for (int index = 0; index < 5; index++) {
            int randomIndex = random.nextInt(TABLE_CODE_ALPHABET.length());
            builder.append(TABLE_CODE_ALPHABET.charAt(randomIndex));
        }
        return builder.toString();
    }

    private void showIdentityPage(
            Scene scene,
            ConnectPlayerService connectPlayerService,
            DisconnectPlayerService disconnectPlayerService,
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
                identityView.showValidationMessage("Impossible de contacter le serveur.");
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
                    disconnectPlayerService,
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
            DisconnectPlayerService disconnectPlayerService,
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
                    disconnectPlayerService,
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
                    disconnectPlayerService,
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
            DisconnectPlayerService disconnectPlayerService,
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
                                disconnectPlayerService,
                                "Impossible de charger l'etat de la table."
                        );
                        return;
                    }

                    scene.setRoot(new PokerTableView(
                            initialState,
                            assetLoader,
                            () -> {
                                disconnectPlayerService.disconnectPlayer();
                                showIdentityPage(
                                        scene,
                                        connectPlayerService,
                                        disconnectPlayerService,
                                        createTableService,
                                        joinTableService,
                                        tableCodeValidator,
                                        loadTableStateService,
                                        assetLoader,
                                        null
                                );
                            }
                    ));
                }));
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
