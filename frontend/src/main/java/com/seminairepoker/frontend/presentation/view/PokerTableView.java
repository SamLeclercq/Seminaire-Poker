package com.seminairepoker.frontend.presentation.view;

import com.seminairepoker.frontend.infrastructure.assets.AssetLoader;
import com.seminairepoker.frontend.presentation.state.PlayerSeatUiState;
import com.seminairepoker.frontend.presentation.state.TableUiState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

public class PokerTableView extends BorderPane {
    private static final double[] TWO_PLAYER_ANGLES = {-90, 90};
    private static final double[] THREE_PLAYER_ANGLES = {-90, 30, 150};
    private static final double[] FOUR_PLAYER_ANGLES = {-90, 0, 90, 180};
    private static final double[] FIVE_PLAYER_ANGLES = {-90, -18, 54, 126, 198};
    private static final double[] SIX_PLAYER_ANGLES = {-150, -90, -30, 30, 90, 150};
    private final Node tableNode;
    private final Pane seatOverlay;
    private final List<PlayerSeatView> seatViews;
    private final CommunityCardsView communityCardsView;
    private final PlayerHandView playerHandView;
    private final ActionBarView actionBarView;

    public PokerTableView(TableUiState state, AssetLoader assetLoader) {
        this(state, assetLoader, () -> { }, () -> { }, () -> { }, () -> { }, amount -> { }, amount -> { });
    }

    public PokerTableView(TableUiState state, AssetLoader assetLoader, Runnable onReturnHomeRequested) {
        this(state, assetLoader, onReturnHomeRequested, () -> { }, () -> { }, () -> { }, amount -> { }, amount -> { });
    }

    public PokerTableView(TableUiState state, AssetLoader assetLoader, Runnable onReturnHomeRequested, Runnable onReadyRequested) {
        this(state, assetLoader, onReturnHomeRequested, onReadyRequested, () -> { }, () -> { }, amount -> { }, amount -> { });
    }

    public PokerTableView(
            TableUiState state,
            AssetLoader assetLoader,
            Runnable onReturnHomeRequested,
            Runnable onReadyRequested,
            Runnable onCheckRequested,
            Runnable onFoldRequested,
            IntConsumer onBetRequested,
            IntConsumer onRaiseRequested
    ) {
        getStyleClass().add("table-screen");
        setPadding(new Insets(18));

        tableNode = assetLoader.loadTable(980, 520);
        communityCardsView = new CommunityCardsView(state.communityCards(), assetLoader);
        playerHandView = new PlayerHandView(state.localPlayerCards(), assetLoader);
        actionBarView = new ActionBarView(onReadyRequested, onCheckRequested, onFoldRequested, onBetRequested, onRaiseRequested);
        actionBarView.applyReadyState(state.waitingForReady(), state.localPlayerReady());
        actionBarView.applyActionState(state.legalActions(), state.currentBet(), state.localPlayerStack());
        seatOverlay = new Pane();
        seatViews = createSeatViews(state.seats(), assetLoader);

        setTop(new TableHeaderView(state.tableCode(), onReturnHomeRequested));

        StackPane tableLayer = buildTableLayer(state);
        setCenter(tableLayer);

        VBox footer = new VBox(10, playerHandView, actionBarView);
        footer.getStyleClass().add("table-footer");
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(14, 0, 0, 0));
        setBottom(footer);

        tableLayer.widthProperty().addListener((observable, oldValue, newValue) -> refreshResponsiveLayout(tableLayer));
        tableLayer.heightProperty().addListener((observable, oldValue, newValue) -> refreshResponsiveLayout(tableLayer));
        refreshResponsiveLayout(tableLayer);
    }

    private StackPane buildTableLayer(TableUiState state) {
        StackPane tableLayer = new StackPane();
        tableLayer.getStyleClass().add("table-layer");
        tableLayer.setMinHeight(420);

        VBox centerContent = new VBox(16);
        centerContent.getStyleClass().add("table-center-content");
        centerContent.setAlignment(Pos.CENTER);
        centerContent.getChildren().addAll(
                new PotView(state.pot(), state.roundLabel()),
                communityCardsView
        );

        seatOverlay.setPickOnBounds(false);
        seatOverlay.getStyleClass().add("seat-overlay");
        seatOverlay.getChildren().addAll(seatViews);

        tableLayer.getChildren().addAll(tableNode, centerContent, seatOverlay);
        return tableLayer;
    }

    private List<PlayerSeatView> createSeatViews(List<PlayerSeatUiState> seats, AssetLoader assetLoader) {
        List<PlayerSeatView> views = new ArrayList<>();
        for (int index = 0; index < Math.min(6, seats.size()); index++) {
            views.add(new PlayerSeatView(seats.get(index), assetLoader));
        }
        return views;
    }

    private void refreshResponsiveLayout(StackPane tableLayer) {
        double availableWidth = Math.max(700, tableLayer.getWidth() - 30);
        double availableHeight = Math.max(380, tableLayer.getHeight() - 20);

        double tableWidth = Math.min(availableWidth, availableHeight * 1.9);
        double tableHeight = tableWidth * 0.53;
        tableHeight = Math.min(tableHeight, availableHeight);

        applyTableNodeSize(tableWidth, tableHeight);
        seatOverlay.setPrefSize(tableWidth, tableHeight);
        layoutSeats(tableWidth, tableHeight);

        double scale = clamp(tableWidth / 980.0, 0.72, 1.25);
        communityCardsView.setCardSize(86 * scale, 124 * scale);
        communityCardsView.setSpacing(10 * scale);

        playerHandView.setCardSize(110 * scale, 160 * scale);
        playerHandView.setSpacing(14 * scale);

        actionBarView.setSpacing(12 * scale);
        actionBarView.setButtonScale(scale);
    }

    private void applyTableNodeSize(double width, double height) {
        if (tableNode instanceof ImageView imageView) {
            imageView.setFitWidth(width);
            imageView.setFitHeight(height);
            return;
        }
        if (tableNode instanceof Region region) {
            region.setPrefSize(width, height);
        }
    }

    private void layoutSeats(double tableWidth, double tableHeight) {
        double centerX = tableWidth / 2;
        double centerY = tableHeight / 2;
        double radiusX = tableWidth * 0.42;
        double radiusY = tableHeight * 0.39;
        double scale = clamp(tableWidth / 980.0, 0.72, 1.25);
        double seatWidth = 186 * scale;
        double seatHeight = 126 * scale;

        double[] seatAngles = resolveSeatAngles(seatViews.size());
        for (int index = 0; index < seatViews.size(); index++) {
            double angle = Math.toRadians(seatAngles[index % seatAngles.length]);
            double x = centerX + radiusX * Math.cos(angle) - (seatWidth / 2);
            double y = centerY + radiusY * Math.sin(angle) - (seatHeight / 2);

            PlayerSeatView seatView = seatViews.get(index);
            seatView.setSeatSize(seatWidth, seatHeight);
            seatView.relocate(x, y);
        }
    }

    private double[] resolveSeatAngles(int seatCount) {
        return switch (seatCount) {
            case 2 -> TWO_PLAYER_ANGLES;
            case 3 -> THREE_PLAYER_ANGLES;
            case 4 -> FOUR_PLAYER_ANGLES;
            case 5 -> FIVE_PLAYER_ANGLES;
            default -> SIX_PLAYER_ANGLES;
        };
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
