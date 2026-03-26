package com.seminairepoker.frontend.infrastructure.websocket.config;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WsEndpointResolverTest {

    @Test
    void shouldUseExplicitWsUrl_whenProvided() {
        // Arrange
        Map<String, String> environment = Map.of(
                "POKER_WS_URL", "ws://example.org:9999"
        );

        // Act
        URI endpoint = WsEndpointResolver.resolve(environment);

        // Assert
        assertEquals(URI.create("ws://example.org:9999"), endpoint);
    }

    @Test
    void shouldBuildEndpointFromHostAndPort_whenWsUrlIsMissing() {
        // Arrange
        Map<String, String> environment = Map.of(
                "POKER_WS_HOST", "backend.local",
                "POKER_WS_PORT", "9876"
        );

        // Act
        URI endpoint = WsEndpointResolver.resolve(environment);

        // Assert
        assertEquals(URI.create("ws://backend.local:9876"), endpoint);
    }

    @Test
    void shouldUseDefaults_whenNoEnvironmentVariableIsProvided() {
        // Arrange
        Map<String, String> environment = Map.of();

        // Act
        URI endpoint = WsEndpointResolver.resolve(environment);

        // Assert
        assertEquals(URI.create("ws://localhost:8765"), endpoint);
    }

    @Test
    void shouldIgnoreBlankWsUrl_andFallbackToHostAndPort() {
        // Arrange
        Map<String, String> environment = Map.of(
                "POKER_WS_URL", "   ",
                "POKER_WS_HOST", "127.0.0.1",
                "POKER_WS_PORT", "9000"
        );

        // Act
        URI endpoint = WsEndpointResolver.resolve(environment);

        // Assert
        assertEquals(URI.create("ws://127.0.0.1:9000"), endpoint);
    }
}

