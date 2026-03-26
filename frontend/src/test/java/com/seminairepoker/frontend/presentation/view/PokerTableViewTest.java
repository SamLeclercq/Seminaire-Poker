package com.seminairepoker.frontend.presentation.view;

import com.seminairepoker.frontend.infrastructure.assets.AssetLoader;
import com.seminairepoker.frontend.presentation.state.PlayerSeatUiState;
import com.seminairepoker.frontend.presentation.state.TableUiState;
import com.seminairepoker.frontend.support.FxUiTestSupport;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PokerTableViewTest extends FxUiTestSupport {

    @Test
    void shouldRequestTwoLocalCards_whenTableRendersPlayerHand() throws Exception {
        // Arrange
        RecordingAssetLoader assetLoader = new RecordingAssetLoader();
        TableUiState state = new TableUiState(
                "AB123",
                "preflop",
                30,
                List.of(),
                List.of("ace_of_spades", "ace_of_hearts"),
                List.of(new PlayerSeatUiState(1, "Nina", 1_540, false, true, false, true, true))
        );

        // Act
        List<String> requestedCodes = onFxThread(() -> {
            new PokerTableView(state, assetLoader);
            return List.copyOf(assetLoader.requestedCardCodes());
        });

        // Assert
        assertTrue(requestedCodes.contains("ace_of_spades"));
        assertTrue(requestedCodes.contains("ace_of_hearts"));
    }

    @Test
    void shouldShowTableIdentifier_whenTableIsRendered() throws Exception {
        // Arrange
        TableUiState state = new TableUiState(
                "AB123",
                "Flop",
                240,
                List.of("10_of_hearts", "10_of_spades", "2_of_clubs", "3_of_diamonds", "5_of_hearts"),
                List.of("ace_of_spades", "ace_of_hearts"),
                List.of(new PlayerSeatUiState(1, "Nina", 1_540, false, true, false))
        );

        // Act
        String headerText = onFxThread(() -> {
            PokerTableView pokerTableView = new PokerTableView(state, new AssetLoader());
            return findLabelByStyleClass(pokerTableView, "table-id-label").getText();
        });

        // Assert
        assertEquals("Table AB123", headerText);
    }

    @Test
    void shouldTriggerReturnHomeCallback_whenReturnHomeButtonIsClicked() throws Exception {
        // Arrange
        TableUiState state = new TableUiState(
                "AB123",
                "Flop",
                240,
                List.of("10_of_hearts", "10_of_spades", "2_of_clubs", "3_of_diamonds", "5_of_hearts"),
                List.of("ace_of_spades", "ace_of_hearts"),
                List.of(new PlayerSeatUiState(1, "Nina", 1_540, false, true, false))
        );
        AtomicBoolean callbackTriggered = new AtomicBoolean(false);

        // Act
        onFxThread(() -> {
            PokerTableView pokerTableView = new PokerTableView(state, new AssetLoader(), () -> callbackTriggered.set(true));
            findButtonByText(pokerTableView, "Retour accueil").fire();
            return null;
        });

        // Assert
        assertTrue(callbackTriggered.get());
    }

    @Test
    void shouldTriggerReadyCallback_whenReadyButtonIsClicked() throws Exception {
        // Arrange
        TableUiState state = new TableUiState(
                "AB123",
                "waiting",
                240,
                List.of("10_of_hearts", "10_of_spades", "2_of_clubs", "3_of_diamonds", "5_of_hearts"),
                List.of("ace_of_spades", "ace_of_hearts"),
                List.of(new PlayerSeatUiState(1, "Nina", 1_540, false, true, false, true, false))
        );
        AtomicBoolean callbackTriggered = new AtomicBoolean(false);

        // Act
        onFxThread(() -> {
            PokerTableView pokerTableView = new PokerTableView(
                    state,
                    new AssetLoader(),
                    () -> { },
                    () -> callbackTriggered.set(true)
            );
            findButtonByText(pokerTableView, "Pret").fire();
            return null;
        });

        // Assert
        assertTrue(callbackTriggered.get());
    }

    @Test
    void shouldDistributeSeatsAcrossTopAndBottom_whenTableIsLaidOut() throws Exception {
        // Arrange
        TableUiState state = new TableUiState(
                "AB123",
                "Flop",
                240,
                List.of("10_of_hearts", "10_of_spades", "2_of_clubs"),
                List.of("ace_of_spades", "ace_of_hearts"),
                List.of(
                        new PlayerSeatUiState(1, "A", 1000, false, true, false),
                        new PlayerSeatUiState(2, "B", 1000, false, true, false),
                        new PlayerSeatUiState(3, "C", 1000, false, true, false),
                        new PlayerSeatUiState(4, "D", 1000, false, true, false),
                        new PlayerSeatUiState(5, "E", 1000, false, true, false),
                        new PlayerSeatUiState(6, "F", 1000, false, true, false)
                )
        );

        // Act
        List<Double> seatCentersY = onFxThread(() -> {
            PokerTableView pokerTableView = new PokerTableView(state, new AssetLoader());
            pokerTableView.resize(1200, 800);
            pokerTableView.applyCss();
            pokerTableView.layout();

            Pane seatOverlay = (Pane) findNodeByStyleClass(pokerTableView, "seat-overlay");
            return seatOverlay.getChildren()
                    .stream()
                    .map(node -> node.getLayoutY() + node.prefHeight(-1) / 2)
                    .toList();
        });

        // Assert
        long topHalfCount = seatCentersY.stream().filter(y -> y < 250).count();
        long bottomHalfCount = seatCentersY.stream().filter(y -> y > 250).count();
        assertTrue(topHalfCount >= 2);
        assertTrue(bottomHalfCount >= 2);
    }

    @Test
    void shouldPlaceOneSeatNearTopCenterAndBottomCenter_whenSixSeatsAreDisplayed() throws Exception {
        // Arrange
        TableUiState state = new TableUiState(
                "AB123",
                "Flop",
                240,
                List.of("10_of_hearts", "10_of_spades", "2_of_clubs"),
                List.of("ace_of_spades", "ace_of_hearts"),
                List.of(
                        new PlayerSeatUiState(1, "A", 1000, false, true, false),
                        new PlayerSeatUiState(2, "B", 1000, false, true, false),
                        new PlayerSeatUiState(3, "C", 1000, false, true, false),
                        new PlayerSeatUiState(4, "D", 1000, false, true, false),
                        new PlayerSeatUiState(5, "E", 1000, false, true, false),
                        new PlayerSeatUiState(6, "F", 1000, false, true, false)
                )
        );

        // Act
        List<double[]> seatCenters = onFxThread(() -> {
            PokerTableView pokerTableView = new PokerTableView(state, new AssetLoader());
            pokerTableView.resize(1200, 800);
            pokerTableView.applyCss();
            pokerTableView.layout();

            Pane seatOverlay = (Pane) findNodeByStyleClass(pokerTableView, "seat-overlay");
            return seatOverlay.getChildren()
                    .stream()
                    .map(node -> new double[]{
                            node.getLayoutX() + node.prefWidth(-1) / 2,
                            node.getLayoutY() + node.prefHeight(-1) / 2
                    })
                    .toList();
        });

        // Assert
        double tableCenterX = 585;
        long nearTopCenterCount = seatCenters.stream().filter(center -> center[0] > tableCenterX - 90 && center[0] < tableCenterX + 90 && center[1] < 180).count();
        long nearBottomCenterCount = seatCenters.stream().filter(center -> center[0] > tableCenterX - 90 && center[0] < tableCenterX + 90 && center[1] > 350).count();

        assertTrue(nearTopCenterCount >= 1);
        assertTrue(nearBottomCenterCount >= 1);
    }

    @Test
    void shouldPlaceFourSeatsOnCardinalPositions_whenFourSeatsAreDisplayed() throws Exception {
        // Arrange
        TableUiState state = new TableUiState(
                "AB123",
                "Flop",
                240,
                List.of("10_of_hearts", "10_of_spades", "2_of_clubs"),
                List.of("ace_of_spades", "ace_of_hearts"),
                List.of(
                        new PlayerSeatUiState(1, "A", 1000, false, true, false),
                        new PlayerSeatUiState(2, "B", 1000, false, true, false),
                        new PlayerSeatUiState(3, "C", 1000, false, true, false),
                        new PlayerSeatUiState(4, "D", 1000, false, true, false)
                )
        );

        // Act
        List<double[]> seatCenters = onFxThread(() -> {
            PokerTableView pokerTableView = new PokerTableView(state, new AssetLoader());
            pokerTableView.resize(1200, 800);
            pokerTableView.applyCss();
            pokerTableView.layout();

            Pane seatOverlay = (Pane) findNodeByStyleClass(pokerTableView, "seat-overlay");
            return seatOverlay.getChildren()
                    .stream()
                    .map(node -> new double[]{
                            node.getLayoutX() + node.prefWidth(-1) / 2,
                            node.getLayoutY() + node.prefHeight(-1) / 2
                    })
                    .toList();
        });

        // Assert
        double tableCenterX = 585;
        double tableCenterY = 250;
        long topCenterCount = seatCenters.stream().filter(center -> center[0] > tableCenterX - 85 && center[0] < tableCenterX + 85 && center[1] < tableCenterY - 120).count();
        long bottomCenterCount = seatCenters.stream().filter(center -> center[0] > tableCenterX - 85 && center[0] < tableCenterX + 85 && center[1] > tableCenterY + 120).count();
        long leftCenterCount = seatCenters.stream().filter(center -> center[0] < tableCenterX - 220 && center[1] > tableCenterY - 70 && center[1] < tableCenterY + 70).count();
        long rightCenterCount = seatCenters.stream().filter(center -> center[0] > tableCenterX + 220 && center[1] > tableCenterY - 70 && center[1] < tableCenterY + 70).count();

        assertTrue(topCenterCount >= 1);
        assertTrue(bottomCenterCount >= 1);
        assertTrue(leftCenterCount >= 1);
        assertTrue(rightCenterCount >= 1);
    }

    private Label findLabelByStyleClass(Node node, String styleClass) {
        if (node instanceof Label label && label.getStyleClass().contains(styleClass)) {
            return label;
        }
        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                Label nested = findLabelByStyleClass(child, styleClass);
                if (nested != null) {
                    return nested;
                }
            }
        }
        return null;
    }

    private Button findButtonByText(Node node, String text) {
        if (node instanceof Button button && text.equals(button.getText())) {
            return button;
        }
        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                Button nested = findButtonByText(child, text);
                if (nested != null) {
                    return nested;
                }
            }
        }
        return null;
    }

    private Node findNodeByStyleClass(Node node, String styleClass) {
        if (node.getStyleClass().contains(styleClass)) {
            return node;
        }
        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                Node nested = findNodeByStyleClass(child, styleClass);
                if (nested != null) {
                    return nested;
                }
            }
        }
        return null;
    }

    private static final class RecordingAssetLoader extends AssetLoader {
        private final List<String> requestedCardCodes = new ArrayList<>();

        @Override
        public Node loadCard(String cardCode, double width, double height) {
            requestedCardCodes.add(cardCode);
            return new Pane();
        }

        List<String> requestedCardCodes() {
            return requestedCardCodes;
        }
    }
}
