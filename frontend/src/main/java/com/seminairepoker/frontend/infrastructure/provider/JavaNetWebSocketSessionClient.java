package com.seminairepoker.frontend.infrastructure.provider;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Duration;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public final class JavaNetWebSocketSessionClient implements WebSocketSessionClient {
    private final HttpClient httpClient;
    private final LinkedBlockingQueue<String> inboundMessages;

    private WebSocket webSocket;

    public JavaNetWebSocketSessionClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.inboundMessages = new LinkedBlockingQueue<>();
    }

    @Override
    public synchronized void open(URI endpointUri, Duration timeout) throws Exception {
        if (isOpen()) {
            return;
        }

        webSocket = httpClient.newWebSocketBuilder()
                .connectTimeout(timeout)
                .buildAsync(endpointUri, new QueueingListener(inboundMessages))
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
        private final StringBuilder payloadBuilder;

        private QueueingListener(LinkedBlockingQueue<String> inboundMessages) {
            this.inboundMessages = inboundMessages;
            this.payloadBuilder = new StringBuilder();
        }

        @Override
        public java.util.concurrent.CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            payloadBuilder.append(data);
            if (last) {
                inboundMessages.offer(payloadBuilder.toString());
                payloadBuilder.setLength(0);
            }
            return WebSocket.Listener.super.onText(webSocket, data, last);
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            inboundMessages.offer("{\"status\":\"error\",\"message\":\"" + error.getMessage() + "\"}");
        }
    }
}


