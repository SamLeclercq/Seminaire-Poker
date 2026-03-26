package com.seminairepoker.frontend.infrastructure.websocket.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JavaNetWebSocketSessionClientTest {

    @Test
    void shouldClassifyActionResponseAsDirect_whenMessageIsSuccessForPlayerAction() {
        // Arrange
        String payload = "{\"status\":\"success\",\"action\":\"call\",\"data\":{\"tableId\":\"AB123\"}}";

        // Act / Assert
        assertTrue(JavaNetWebSocketSessionClient.isDirectResponsePayload(payload));
    }

    @Test
    void shouldClassifyErrorResponseAsDirect_whenMessageContainsErrorStatus() {
        // Arrange
        String payload = "{\"status\":\"error\",\"message\":\"invalid action\"}";

        // Act / Assert
        assertTrue(JavaNetWebSocketSessionClient.isDirectResponsePayload(payload));
    }

    @Test
    void shouldClassifyShowdownAsPush_whenMessageIsSuccessButNotActionResponse() {
        // Arrange
        String payload = "{\"status\":\"success\",\"action\":\"showdown\",\"data\":{\"tableId\":\"AB123\"}}";

        // Act / Assert
        assertFalse(JavaNetWebSocketSessionClient.isDirectResponsePayload(payload));
    }

    @Test
    void shouldClassifyDisconnectAsPush_whenMessageIsBroadcastEvent() {
        // Arrange
        String payload = "{\"status\":\"success\",\"action\":\"disconnect\",\"data\":{\"tableId\":\"AB123\"}}";

        // Act / Assert
        assertFalse(JavaNetWebSocketSessionClient.isDirectResponsePayload(payload));
    }

    @Test
    void shouldClassifyRawStateAsPush_whenStatusFieldIsMissing() {
        // Arrange
        String payload = "{\"tableId\":\"AB123\",\"currentState\":\"preflop\",\"players\":[]}";

        // Act / Assert
        assertFalse(JavaNetWebSocketSessionClient.isDirectResponsePayload(payload));
    }
}

