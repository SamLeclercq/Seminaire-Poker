package com.seminairepoker.frontend.presentation.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntConsumer;

public class ActionBarView extends VBox {
    private static final double BASE_BUTTON_WIDTH = 140;
    private static final int DEFAULT_MIN_BET = 100;

    private final Button foldButton;
    private final Button checkButton;
    private final Button callButton;
    private final Button betButton;
    private final Button raiseButton;
    private final Button readyButton;
    private final Button amountApplyButton;
    private final Slider amountSlider;
    private final Label amountValueLabel;
    private final Label amountHintLabel;
    private final HBox actionsRow;
    private final HBox amountRow;

    private final IntConsumer onBetRequested;
    private final IntConsumer onRaiseRequested;
    private Set<String> currentActions;
    private int currentBetAmount;
    private int currentLocalPlayerStack;
    private Mode pendingAmountMode;

    private enum Mode {
        BET,
        RAISE
    }

    public ActionBarView() {
        this(() -> { }, () -> { }, () -> { }, () -> { }, amount -> { }, amount -> { });
    }

    public ActionBarView(Runnable onReadyRequested) {
        this(onReadyRequested, () -> { }, () -> { }, () -> { }, amount -> { }, amount -> { });
    }

    public ActionBarView(
            Runnable onReadyRequested,
            Runnable onCheckRequested,
            Runnable onCallRequested,
            Runnable onFoldRequested,
            IntConsumer onBetRequested,
            IntConsumer onRaiseRequested
    ) {
        Runnable safeOnReadyRequested = Objects.requireNonNull(onReadyRequested, "onReadyRequested must not be null");
        Runnable safeOnCheckRequested = Objects.requireNonNull(onCheckRequested, "onCheckRequested must not be null");
        Runnable safeOnCallRequested = Objects.requireNonNull(onCallRequested, "onCallRequested must not be null");
        Runnable safeOnFoldRequested = Objects.requireNonNull(onFoldRequested, "onFoldRequested must not be null");
        this.onBetRequested = Objects.requireNonNull(onBetRequested, "onBetRequested must not be null");
        this.onRaiseRequested = Objects.requireNonNull(onRaiseRequested, "onRaiseRequested must not be null");
        this.currentActions = Set.of();
        this.currentBetAmount = 0;
        this.currentLocalPlayerStack = 0;

        getStyleClass().add("action-bar");
        setAlignment(Pos.CENTER);
        setSpacing(8);
        setPadding(new Insets(8, 0, 0, 0));

        foldButton = buildActionButton("Fold", "action-danger");
        foldButton.setOnAction(event -> safeOnFoldRequested.run());

        checkButton = buildActionButton("Check", "action-neutral");
        checkButton.setOnAction(event -> safeOnCheckRequested.run());

        callButton = buildActionButton("Call", "action-primary");
        callButton.setOnAction(event -> safeOnCallRequested.run());

        betButton = buildActionButton("Bet", "action-primary");
        betButton.setOnAction(event -> showAmountSelector(Mode.BET));

        raiseButton = buildActionButton("Raise", "action-strong");
        raiseButton.setOnAction(event -> showAmountSelector(Mode.RAISE));

        readyButton = buildActionButton("Pret", "action-ready-pending");
        readyButton.setOnAction(event -> safeOnReadyRequested.run());

        actionsRow = new HBox(12, foldButton, checkButton, callButton, betButton, raiseButton, readyButton);
        actionsRow.setAlignment(Pos.CENTER);

        amountHintLabel = new Label();
        amountHintLabel.getStyleClass().add("action-slider-label");

        amountSlider = new Slider(0, 1, 0);
        amountSlider.getStyleClass().add("action-slider");
        amountSlider.setBlockIncrement(1);
        amountSlider.setMajorTickUnit(1);
        amountSlider.setMinorTickCount(0);
        amountSlider.setSnapToTicks(true);
        amountSlider.valueProperty().addListener((observable, oldValue, newValue) -> updateAmountValueLabel());

        amountValueLabel = new Label("0");
        amountValueLabel.getStyleClass().add("action-slider-value");

        amountApplyButton = buildActionButton("Valider", "action-primary");
        amountApplyButton.setPrefWidth(BASE_BUTTON_WIDTH * 0.9);
        amountApplyButton.setOnAction(event -> submitAmountAction());

        amountRow = new HBox(10, amountHintLabel, amountSlider, amountValueLabel, amountApplyButton);
        amountRow.getStyleClass().add("action-slider-row");
        amountRow.setAlignment(Pos.CENTER);
        amountRow.setVisible(false);
        amountRow.setManaged(false);

        getChildren().addAll(actionsRow, amountRow);

        applyReadyState(false, false);
        applyActionState(List.of(), 0, 0);
    }

    private Button buildActionButton(String text, String cssClass) {
        Button button = new Button(text);
        button.getStyleClass().addAll("action-button", cssClass);
        button.setPrefWidth(BASE_BUTTON_WIDTH);
        return button;
    }

    public void setButtonScale(double scale) {
        double buttonWidth = BASE_BUTTON_WIDTH * scale;
        actionsRow.getChildren().forEach(node -> {
            if (node instanceof Button button) {
                button.setPrefWidth(buttonWidth);
            }
        });
        amountApplyButton.setPrefWidth(buttonWidth * 0.9);
        amountSlider.setPrefWidth(buttonWidth * 1.7);
    }

    public void applyReadyState(boolean waitingForReady, boolean localPlayerReady) {
        readyButton.getStyleClass().removeAll("action-ready-pending", "action-ready-active");
        if (localPlayerReady) {
            readyButton.getStyleClass().add("action-ready-active");
        } else {
            readyButton.getStyleClass().add("action-ready-pending");
        }
        readyButton.setDisable(!waitingForReady || localPlayerReady);
        readyButton.setVisible(waitingForReady);
        readyButton.setManaged(waitingForReady);
    }

    public void applyActionState(List<String> legalActions, int currentBet, int localPlayerStack) {
        Set<String> actionSet = new HashSet<>();
        if (legalActions != null) {
            legalActions.stream()
                    .filter(action -> action != null && !action.isBlank())
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .forEach(actionSet::add);
        }

        currentActions = Set.copyOf(actionSet);
        currentBetAmount = Math.max(0, currentBet);
        currentLocalPlayerStack = Math.max(0, localPlayerStack);

        foldButton.setDisable(!actionSet.contains("fold"));
        checkButton.setDisable(!actionSet.contains("check"));
        callButton.setDisable(!actionSet.contains("call"));

        boolean canBet = actionSet.contains("bet");
        boolean canRaise = actionSet.contains("raise");
        betButton.setDisable(!canBet);
        raiseButton.setDisable(!canRaise);

        if (pendingAmountMode == Mode.BET && canBet) {
            configureAmountRange(Mode.BET, currentBet, localPlayerStack);
        } else if (pendingAmountMode == Mode.RAISE && canRaise) {
            configureAmountRange(Mode.RAISE, currentBet, localPlayerStack);
        } else {
            hideAmountSelector();
        }
    }

    private void showAmountSelector(Mode mode) {
        if ((mode == Mode.BET && !currentActions.contains("bet")) || (mode == Mode.RAISE && !currentActions.contains("raise"))) {
            return;
        }

        pendingAmountMode = mode;
        configureAmountRange(mode, currentBetAmount, currentLocalPlayerStack);
        amountRow.setVisible(true);
        amountRow.setManaged(true);
        amountApplyButton.requestFocus();
    }

    private void configureAmountRange(Mode mode, int currentBet, int localPlayerStack) {
        int maxAmount = Math.max(0, localPlayerStack);
        int minAmount = mode == Mode.RAISE
                ? Math.max(DEFAULT_MIN_BET, currentBet + 1)
                : DEFAULT_MIN_BET;

        if (maxAmount < minAmount) {
            amountSlider.setMin(0);
            amountSlider.setMax(0);
            amountSlider.setValue(0);
            amountSlider.setDisable(true);
            amountApplyButton.setDisable(true);
            amountHintLabel.setText(mode == Mode.RAISE ? "Raise indisponible" : "Bet indisponible");
            amountValueLabel.setText("0");
            return;
        }

        amountSlider.setDisable(false);
        amountApplyButton.setDisable(false);
        amountSlider.setMin(minAmount);
        amountSlider.setMax(maxAmount);
        double clampedValue = Math.max(minAmount, Math.min(maxAmount, amountSlider.getValue()));
        amountSlider.setValue(clampedValue);
        amountHintLabel.setText(mode == Mode.RAISE
                ? "Raise (min " + minAmount + ")"
                : "Bet (min " + minAmount + ")");
        updateAmountValueLabel();
    }

    private void updateAmountValueLabel() {
        amountValueLabel.setText(String.valueOf((int) Math.round(amountSlider.getValue())));
    }

    private void submitAmountAction() {
        int amount = (int) Math.round(amountSlider.getValue());
        if (pendingAmountMode == Mode.BET) {
            onBetRequested.accept(amount);
        } else if (pendingAmountMode == Mode.RAISE) {
            onRaiseRequested.accept(amount);
        }
        hideAmountSelector();
    }

    private void hideAmountSelector() {
        pendingAmountMode = null;
        amountRow.setVisible(false);
        amountRow.setManaged(false);
    }

    public Button readyButton() {
        return readyButton;
    }
}

