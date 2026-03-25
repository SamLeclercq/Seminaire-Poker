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

public class PokerTableView extends BorderPane {
    private final Node tableNode;
    private final Pane seatOverlay;
    private final List<PlayerSeatView> seatViews;
    private final CommunityCardsView communityCardsView;
    private final PlayerHandView playerHandView;
    private final ActionBarView actionBarView;

    public PokerTableView(TableUiState state, AssetLoader assetLoader) {
        this(state, assetLoader, () -> { });
    }

    public PokerTableView(TableUiState state, AssetLoader assetLoader, Runnable onLeaveTableRequested) {
        getStyleClass().add("table-screen");
        setPadding(new Insets(18));

        tableNode = assetLoader.loadTable(980, 520);
        communityCardsView = new CommunityCardsView(state.communityCards(), assetLoader);
        playerHandView = new PlayerHandView(state.localPlayerCards(), assetLoader);
        actionBarView = new ActionBarView();
        seatOverlay = new Pane();
        seatViews = createSeatViews(state.seats());

        setTop(new TableHeaderView(state.tableCode(), onLeaveTableRequested));

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

    private List<PlayerSeatView> createSeatViews(List<PlayerSeatUiState> seats) {
        List<PlayerSeatView> views = new ArrayList<>();
        for (int index = 0; index < Math.min(6, seats.size()); index++) {
            views.add(new PlayerSeatView(seats.get(index)));
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
        double radiusX = tableWidth * 0.41;
        double radiusY = tableHeight * 0.43;
        double scale = clamp(tableWidth / 980.0, 0.72, 1.25);
        double seatWidth = 130 * scale;
        double seatHeight = 84 * scale;

        for (int index = 0; index < seatViews.size(); index++) {
            double angle = Math.toRadians(-90 + index * 60);
            double x = centerX + radiusX * Math.cos(angle) - (seatWidth / 2);
            double y = centerY + radiusY * Math.sin(angle) - (seatHeight / 2);

            PlayerSeatView seatView = seatViews.get(index);
            seatView.setSeatSize(seatWidth, seatHeight);
            seatView.relocate(x, y);
        }
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
