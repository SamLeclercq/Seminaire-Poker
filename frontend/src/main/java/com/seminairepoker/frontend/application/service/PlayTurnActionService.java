package com.seminairepoker.frontend.application.service;

import com.seminairepoker.frontend.application.port.PlayerActionPort;

import java.util.Locale;
import java.util.Objects;

public class PlayTurnActionService {
    private final PlayerActionPort playerActionPort;
    private final TableCodeValidator tableCodeValidator;

    public PlayTurnActionService(PlayerActionPort playerActionPort, TableCodeValidator tableCodeValidator) {
        this.playerActionPort = Objects.requireNonNull(playerActionPort, "playerActionPort must not be null");
        this.tableCodeValidator = Objects.requireNonNull(tableCodeValidator, "tableCodeValidator must not be null");
    }

    public boolean check(String tableCode) {
        String normalizedCode = normalizeTableCode(tableCode);
        if (normalizedCode == null) {
            return false;
        }
        return playerActionPort.check(normalizedCode);
    }

    public boolean fold(String tableCode) {
        String normalizedCode = normalizeTableCode(tableCode);
        if (normalizedCode == null) {
            return false;
        }
        return playerActionPort.fold(normalizedCode);
    }

    public boolean bet(String tableCode, int amount) {
        String normalizedCode = normalizeTableCode(tableCode);
        if (normalizedCode == null || amount <= 0) {
            return false;
        }
        return playerActionPort.bet(normalizedCode, amount);
    }

    public boolean raise(String tableCode, int amount) {
        String normalizedCode = normalizeTableCode(tableCode);
        if (normalizedCode == null || amount <= 0) {
            return false;
        }
        return playerActionPort.raise(normalizedCode, amount);
    }

    private String normalizeTableCode(String tableCode) {
        if (!tableCodeValidator.isValid(tableCode)) {
            return null;
        }
        return tableCode.trim().toUpperCase(Locale.ROOT);
    }
}

