package com.seminairepoker.frontend.infrastructure.provider;

import com.seminairepoker.frontend.application.port.JoinTablePort;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;

public class InMemoryJoinTableProvider implements JoinTablePort {
    private final Predicate<String> joinPolicy;

    public InMemoryJoinTableProvider() {
        this(tableCode -> true);
    }

    public InMemoryJoinTableProvider(Predicate<String> joinPolicy) {
        this.joinPolicy = Objects.requireNonNull(joinPolicy, "joinPolicy must not be null");
    }

    @Override
    public boolean joinTable(String tableCode) {
        String normalizedCode = tableCode == null ? "" : tableCode.trim().toUpperCase(Locale.ROOT);
        return joinPolicy.test(normalizedCode);
    }
}

