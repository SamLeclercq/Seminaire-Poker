package com.seminairepoker.frontend.infrastructure.websocket.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Duration;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JavaNetWebSocketSessionClient implements WebSocketSessionClient {
    private static final Pattern STATUS_PATTERN = Pattern.compile("\\\"status\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"");
    private static final Pattern ACTION_PATTERN = Pattern.compile("\\\"action\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"");

    private final HttpClient httpClient;
    private final LinkedBlockingQueue<String> inboundMessages;

    private WebSocket webSocket;
    private Consumer<String> pushMessageListener;

    public JavaNetWebSocketSessionClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.inboundMessages = new LinkedBlockingQueue<>();
        this.pushMessageListener = message -> { };
    }

    @Override
    public synchronized void open(URI endpointUri, Duration timeout) throws Exception {
        if (isOpen()) {
            return;
        }

        webSocket = httpClient.newWebSocketBuilder()
                .connectTimeout(timeout)
                .buildAsync(endpointUri, new QueueingListener(inboundMessages, pushMessageListener))
                .get(timeout.toMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public String sendAndAwait(String message, Duration timeout) throws Exception {
        if (!isOpen()) {
            throw new IllegalStateException("WebSocket session is not open");
        }

        webSocket.sendText(message, true).get(timeout.toMillis(), TimeUnit.MILLISECONDS);

        String response = inboundMessages.poll(timeout.toMillis(), TimeUnit.MILLISECONDS);
        if (response == null) {
            throw new IllegalStateException("No backend websocket response received before timeout");
        }
        return response;
    }

    @Override
    public synchronized boolean isOpen() {
        return webSocket != null && !webSocket.isOutputClosed();
    }

    @Override
    public synchronized void setPushMessageListener(Consumer<String> pushMessageListener) {
        this.pushMessageListener = pushMessageListener == null ? message -> { } : pushMessageListener;
    }

    @Override
    public synchronized void close(Duration timeout) throws Exception {
        if (!isOpen()) {
            return;
        }

        webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "done")
                .get(timeout.toMillis(), TimeUnit.MILLISECONDS);
        webSocket = null;
        inboundMessages.clear();
    }

    private static final class QueueingListener implements WebSocket.Listener {
        private final LinkedBlockingQueue<String> inboundMessages;
        private final Consumer<String> pushMessageListener;
        private final StringBuilder payloadBuilder;

        private QueueingListener(LinkedBlockingQueue<String> inboundMessages, Consumer<String> pushMessageListener) {
            this.inboundMessages = inboundMessages;
            this.pushMessageListener = pushMessageListener;
            this.payloadBuilder = new StringBuilder();
        }

        @Override
        public java.util.concurrent.CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            payloadBuilder.append(data);
            if (last) {
                String payload = payloadBuilder.toString();
                if (isDirectResponsePayload(payload)) {
                    inboundMessages.offer(payload);
                } else {
                    pushMessageListener.accept(payload);
                }
                payloadBuilder.setLength(0);
            }
            return WebSocket.Listener.super.onText(webSocket, data, last);
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            inboundMessages.offer("{\"status\":\"error\",\"message\":\"" + error.getMessage() + "\"}");
        }

    }

    static boolean isDirectResponsePayload(String payload) {
        String status = extractFieldValue(payload, STATUS_PATTERN);
        if (status == null) {
            return false;
        }

        if ("error".equalsIgnoreCase(status)) {
            return true;
        }

        if (!"success".equalsIgnoreCase(status)) {
            return false;
        }

        String action = extractFieldValue(payload, ACTION_PATTERN);
        if (action == null) {
            return false;
        }

        return switch (action.toLowerCase()) {
            case "connect", "create", "join", "leave", "ready", "fold", "check", "call", "bet", "raise" -> true;
            default -> false;
        };
    }

    private static String extractFieldValue(String payload, Pattern pattern) {
        if (payload == null || payload.isBlank()) {
            return null;
        }
        Matcher matcher = pattern.matcher(payload);
        if (!matcher.find()) {
            return null;
        }
        return matcher.group(1);
    }
}

