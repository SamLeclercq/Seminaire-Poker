package com.seminairepoker.frontend.infrastructure.provider;

import java.net.URI;
import java.time.Duration;

public interface WebSocketSessionClient {
    void open(URI endpointUri, Duration timeout) throws Exception;

    String sendAndAwait(String message, Duration timeout) throws Exception;

    boolean isOpen();

    void close(Duration timeout) throws Exception;
}


