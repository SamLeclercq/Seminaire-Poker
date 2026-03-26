package com.seminairepoker.frontend.infrastructure.websocket.client;

import java.net.URI;
import java.time.Duration;
import java.util.function.Consumer;

public interface WebSocketSessionClient {
    void open(URI endpointUri, Duration timeout) throws Exception;

    String sendAndAwait(String message, Duration timeout) throws Exception;

    void setPushMessageListener(Consumer<String> pushMessageListener);

    boolean isOpen();

    void close(Duration timeout) throws Exception;
}

