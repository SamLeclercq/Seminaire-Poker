package com.seminairepoker.frontend.infrastructure.provider;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seminairepoker.frontend.application.port.TableStateProvider;
import com.seminairepoker.frontend.presentation.state.PlayerSeatUiState;
import com.seminairepoker.frontend.presentation.state.TableUiState;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class WebSocketTableStateProvider implements TableStateProvider {
    private static final String LOAD_TABLE_STATE_REQUEST = "{\"type\":\"get_table_state\"}";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(2);
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final String DEFAULT_PORT = "8765";

    private final URI endpointUri;
    private final Duration requestTimeout;
    private final WebSocketMessageClient messageClient;
    private final ObjectMapper objectMapper;

    public WebSocketTableStateProvider() {
        this(resolveEndpointUri(), DEFAULT_TIMEOUT, new JavaNetWebSocketMessageClient());
    }

    public WebSocketTableStateProvider(URI endpointUri, Duration requestTimeout, WebSocketMessageClient messageClient) {
        this.endpointUri = Objects.requireNonNull(endpointUri, "endpointUri must not be null");
        this.requestTimeout = Objects.requireNonNull(requestTimeout, "requestTimeout must not be null");
        this.messageClient = Objects.requireNonNull(messageClient, "messageClient must not be null");
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public TableUiState loadInitialState() {
        try {
            String payload = messageClient.request(endpointUri, LOAD_TABLE_STATE_REQUEST, requestTimeout);
            return parseTableState(payload);
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to load table state from backend websocket", exception);
        }
    }

    private TableUiState parseTableState(String responsePayload) throws JsonProcessingException {
        TableStateMessage tableStateMessage = objectMapper.readValue(responsePayload, TableStateMessage.class);

        if (!"table_state".equals(tableStateMessage.type()) || tableStateMessage.hasMissingRequiredField()) {
            throw new IllegalArgumentException("Invalid table_state payload");
        }

        List<PlayerSeatUiState> seats = tableStateMessage.seats().stream()
                .map(seat -> new PlayerSeatUiState(
                        seat.seatIndex(),
                        seat.playerName(),
                        seat.stack(),
                        seat.dealer(),
                        seat.occupied(),
                        seat.acting()
                ))
                .toList();

        return new TableUiState(
                tableStateMessage.roundLabel(),
                tableStateMessage.pot(),
                List.copyOf(tableStateMessage.communityCards()),
                List.copyOf(tableStateMessage.localPlayerCards()),
                seats
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record TableStateMessage(
            String type,
            String roundLabel,
            Integer pot,
            List<String> communityCards,
            List<String> localPlayerCards,
            List<SeatMessage> seats
    ) {
        private boolean hasMissingRequiredField() {
            return roundLabel == null
                    || pot == null
                    || communityCards == null
                    || localPlayerCards == null
                    || seats == null
                    || seats.stream().anyMatch(Objects::isNull)
                    || seats.stream().anyMatch(SeatMessage::hasMissingRequiredField);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record SeatMessage(
            Integer seatIndex,
            String playerName,
            Integer stack,
            Boolean dealer,
            Boolean occupied,
            Boolean acting
    ) {
        private boolean hasMissingRequiredField() {
            return seatIndex == null
                    || playerName == null
                    || stack == null
                    || dealer == null
                    || occupied == null
                    || acting == null;
        }
    }
}

