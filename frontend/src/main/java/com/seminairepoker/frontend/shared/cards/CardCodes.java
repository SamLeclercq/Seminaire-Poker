package com.seminairepoker.frontend.shared.cards;

import java.util.Locale;

public final class CardCodes {
    public static final String HIDDEN = "card_face_down";

    private CardCodes() {
    }

    public static boolean isHidden(String cardCode) {
        if (cardCode == null || cardCode.isBlank()) {
            return true;
        }
        String normalized = cardCode.trim().toLowerCase(Locale.ROOT);
        return HIDDEN.equals(normalized) || "back".equals(normalized);
    }

    public static String normalizeOrHidden(String cardCode) {
        if (cardCode == null || cardCode.isBlank()) {
            return HIDDEN;
        }
        String normalized = cardCode.trim().toLowerCase(Locale.ROOT).replace(' ', '_').replace('-', '_');
        return "back".equals(normalized) ? HIDDEN : normalized;
    }
}

