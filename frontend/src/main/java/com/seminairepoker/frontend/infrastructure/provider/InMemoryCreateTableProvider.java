package com.seminairepoker.frontend.infrastructure.provider;

import com.seminairepoker.frontend.application.port.CreateTablePort;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class InMemoryCreateTableProvider implements CreateTablePort {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private final Supplier<String> tableCodeGenerator;
    private final Consumer<String> tableCreatedCallback;

    public InMemoryCreateTableProvider() {
        this(defaultGenerator(), tableCode -> { });
    }

    public InMemoryCreateTableProvider(Supplier<String> tableCodeGenerator, Consumer<String> tableCreatedCallback) {
        this.tableCodeGenerator = Objects.requireNonNull(tableCodeGenerator, "tableCodeGenerator must not be null");
        this.tableCreatedCallback = Objects.requireNonNull(tableCreatedCallback, "tableCreatedCallback must not be null");
    }

    @Override
    public String createTable() {
        String tableCode = tableCodeGenerator.get();
        tableCreatedCallback.accept(tableCode);
        return tableCode;
    }

    private static Supplier<String> defaultGenerator() {
        SecureRandom secureRandom = new SecureRandom();
        return () -> {
            StringBuilder builder = new StringBuilder(5);
            for (int index = 0; index < 5; index++) {
                int randomIndex = secureRandom.nextInt(ALPHABET.length());
                builder.append(ALPHABET.charAt(randomIndex));
            }
            return builder.toString();
        };
    }
}

