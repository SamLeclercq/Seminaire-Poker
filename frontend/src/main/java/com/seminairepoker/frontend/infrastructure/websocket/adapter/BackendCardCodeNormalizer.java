package com.seminairepoker.frontend.infrastructure.websocket.adapter;

import com.seminairepoker.frontend.shared.cards.CardCodes;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class BackendCardCodeNormalizer {
    private static final Pattern CARD_TEXT_PATTERN = Pattern.compile("([a-z0-9]+)[_\\s-]*of[_\\s-]*([a-z]+)");
    private static final Set<String> VALID_RANKS = Set.of(
            "2", "3", "4", "5", "6", "7", "8", "9", "10",
            "jack", "queen", "king", "ace"
    );
    private static final Set<String> VALID_SUITS = Set.of("clubs", "diamonds", "hearts", "spades");

    String normalize(Object card) {
        if (card == null) {
            return CardCodes.HIDDEN;
        }

        if (card instanceof Map<?, ?> mapCard) {
            String fromObject = normalizeObjectCard(mapCard);
            if (fromObject != null) {
                return fromObject;
            }
        }

        String raw = String.valueOf(card);
        if (raw == null || raw.isBlank()) {
            return CardCodes.HIDDEN;
        }

        String normalized = CardCodes.normalizeOrHidden(raw);
        if (CardCodes.isHidden(normalized)) {
            return CardCodes.HIDDEN;
        }

        String explicitCode = tryNormalizeCardText(normalized);
        return explicitCode == null ? CardCodes.HIDDEN : explicitCode;
    }

    private String normalizeObjectCard(Map<?, ?> mapCard) {
        String rank = findByKeys(mapCard, "rank", "value");
        String suit = findByKeys(mapCard, "suit", "color");
        if (rank == null || suit == null) {
            return null;
        }

        String normalizedRank = normalizeRank(rank);
        String normalizedSuit = normalizeSuit(suit);
        if (normalizedRank == null || normalizedSuit == null) {
            return null;
        }
        return normalizedRank + "_of_" + normalizedSuit;
    }

    private String findByKeys(Map<?, ?> map, String... keys) {
        for (String key : keys) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() == null) {
                    continue;
                }
                String entryKey = String.valueOf(entry.getKey()).trim().toLowerCase(Locale.ROOT);
                if (!entryKey.equals(key)) {
                    continue;
                }
                if (entry.getValue() == null) {
                    return null;
                }
                return String.valueOf(entry.getValue());
            }
        }
        return null;
    }

    private String tryNormalizeCardText(String text) {
        Matcher matcher = CARD_TEXT_PATTERN.matcher(text);
        if (!matcher.find()) {
            return null;
        }

        String rank = normalizeRank(matcher.group(1));
        String suit = normalizeSuit(matcher.group(2));
        if (rank == null || suit == null) {
            return null;
        }
        return rank + "_of_" + suit;
    }

    private String normalizeRank(String rank) {
        if (rank == null) {
            return null;
        }
        String normalized = rank.trim().toLowerCase(Locale.ROOT)
                .replace("_", "")
                .replace("-", "")
                .replace(" ", "");
        return switch (normalized) {
            case "two" -> "2";
            case "three" -> "3";
            case "four" -> "4";
            case "five" -> "5";
            case "six" -> "6";
            case "seven" -> "7";
            case "eight" -> "8";
            case "nine" -> "9";
            case "a", "ace" -> "ace";
            case "k", "king" -> "king";
            case "q", "queen" -> "queen";
            case "j", "jack" -> "jack";
            case "t", "10", "ten" -> "10";
            default -> VALID_RANKS.contains(normalized) ? normalized : null;
        };
    }

    private String normalizeSuit(String suit) {
        if (suit == null) {
            return null;
        }
        String normalized = suit.trim().toLowerCase(Locale.ROOT)
                .replace("_", "")
                .replace("-", "")
                .replace(" ", "");
        return switch (normalized) {
            case "c", "club", "clubs" -> "clubs";
            case "d", "diamond", "diamonds" -> "diamonds";
            case "h", "heart", "hearts" -> "hearts";
            case "s", "spade", "spades" -> "spades";
            default -> VALID_SUITS.contains(normalized) ? normalized : null;
        };
    }
}

