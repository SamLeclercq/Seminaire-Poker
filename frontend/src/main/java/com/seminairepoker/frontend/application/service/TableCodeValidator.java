package com.seminairepoker.frontend.application.service;

import java.util.Locale;

public class TableCodeValidator {

    public boolean isValid(String tableCode) {
        String normalized = normalize(tableCode);
        return normalized.matches("[A-Z0-9]{5}");
    }

    public String normalize(String tableCode) {
        if (tableCode == null) {
            return "";
        }
        return tableCode.trim().toUpperCase(Locale.ROOT);
    }
}

