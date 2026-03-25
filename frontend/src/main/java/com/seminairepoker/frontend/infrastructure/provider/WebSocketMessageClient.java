package com.seminairepoker.frontend.infrastructure.provider;

import java.net.URI;
import java.time.Duration;

@FunctionalInterface
public interface WebSocketMessageClient {
    String request(URI endpointUri, String requestMessage, Duration requestTimeout) throws Exception;
}

