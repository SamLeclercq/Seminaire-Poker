package com.seminairepoker.frontend.infrastructure.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seminairepoker.frontend.application.model.TableState;
import com.seminairepoker.frontend.application.port.TableStateProvider;

import java.net.URI;
import java.time.Duration;
import java.util.Objects;

public class WebSocketTableStateProvider implements TableStateProvider {
    private static final String LOAD_TABLE_STATE_REQUEST = "{\"action\":\"table_state\",\"payload\":{}}";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(2);
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final String DEFAULT_PORT = "8765";

    private final URI endpointUri;
    private final Duration requestTimeout;
    private final WebSocketMessageClient messageClient;
    private final ObjectMapper objectMapper;
    private final BackendTableStateAdapter backendTableStateAdapter;

    public WebSocketTableStateProvider() {
        this(resolveEndpointUri(), DEFAULT_TIMEOUT, new JavaNetWebSocketMessageClient());
    }

    public WebSocketTableStateProvider(URI endpointUri, Duration requestTimeout, WebSocketMessageClient messageClient) {
        this.endpointUri = Objects.requireNonNull(endpointUri, "endpointUri must not be null");
        this.requestTimeout = Objects.requireNonNull(requestTimeout, "requestTimeout must not be null");
        this.messageClient = Objects.requireNonNull(messageClient, "messageClient must not be null");
        this.objectMapper = new ObjectMapper();
        this.backendTableStateAdapter = new BackendTableStateAdapter();
    }

    @Override
    public TableState loadInitialState() {
        try {
            String payload = messageClient.request(endpointUri, LOAD_TABLE_STATE_REQUEST, requestTimeout);
            return parseTableState(payload);
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to load table state from backend websocket", exception);
        }
    }

    private TableState parseTableState(String responsePayload) throws JsonProcessingException {
        BackendTableStateTransport transport = objectMapper.readValue(responsePayload, BackendTableStateTransport.class);
        if (transport.status() != null && "error".equals(transport.status())) {
            throw new IllegalArgumentException("Backend returned an error response");
        }

        BackendTableStatePayloadTransport payload = resolvePayload(transport);
        if (payload == null) {
            throw new IllegalArgumentException("Invalid table state payload");
        }

        return backendTableStateAdapter.toTableState(payload);
    }

    private BackendTableStatePayloadTransport resolvePayload(BackendTableStateTransport transport) {
        if (transport.data() != null) {
            return transport.data();
        }

        boolean hasDirectGameState = transport.tableId() != null
                || transport.currentState() != null
                || transport.pot() != null
                || transport.communityCards() != null
                || transport.playerPocket() != null
                || transport.players() != null;
        if (!hasDirectGameState) {
            return null;
        }

        return new BackendTableStatePayloadTransport(
                transport.tableId(),
                transport.currentState(),
                transport.pot(),
                transport.communityCards(),
                transport.playerPocket(),
                transport.players()
        );
    }

    private static URI resolveEndpointUri() {
        String wsUrl = System.getenv("POKER_WS_URL");
        if (wsUrl != null && !wsUrl.isBlank()) {
            return URI.create(wsUrl);
        }

        String host = System.getenv().getOrDefault("POKER_WS_HOST", DEFAULT_HOST);
        String port = System.getenv().getOrDefault("POKER_WS_PORT", DEFAULT_PORT);
        return URI.create("ws://" + host + ":" + port);
    }

}
