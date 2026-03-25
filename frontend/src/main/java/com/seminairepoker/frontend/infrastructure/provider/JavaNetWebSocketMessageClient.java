package com.seminairepoker.frontend.infrastructure.provider;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

final class JavaNetWebSocketMessageClient implements WebSocketMessageClient {

    @Override
    public String request(URI endpointUri, String requestMessage, Duration requestTimeout) throws Exception {
        HttpClient httpClient = HttpClient.newHttpClient();
        CompletableFuture<String> responseFuture = new CompletableFuture<>();

        WebSocket.Listener listener = new SingleMessageListener(responseFuture);

        WebSocket webSocket = httpClient.newWebSocketBuilder()
                .connectTimeout(requestTimeout)
                .buildAsync(endpointUri, listener)
                .get(requestTimeout.toMillis(), TimeUnit.MILLISECONDS);

        webSocket.sendText(requestMessage, true)
                .get(requestTimeout.toMillis(), TimeUnit.MILLISECONDS);

        String response = responseFuture.get(requestTimeout.toMillis(), TimeUnit.MILLISECONDS);

        webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "done")
                .get(requestTimeout.toMillis(), TimeUnit.MILLISECONDS);

        return response;
    }

    private static final class SingleMessageListener implements WebSocket.Listener {
        private final CompletableFuture<String> responseFuture;
        private final StringBuilder payloadBuilder;

        private SingleMessageListener(CompletableFuture<String> responseFuture) {
            this.responseFuture = responseFuture;
            this.payloadBuilder = new StringBuilder();
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            payloadBuilder.append(data);
            if (last && !responseFuture.isDone()) {
                responseFuture.complete(payloadBuilder.toString());
            }
            return WebSocket.Listener.super.onText(webSocket, data, last);
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            if (!responseFuture.isDone()) {
                responseFuture.completeExceptionally(error);
            }
        }
    }
}

