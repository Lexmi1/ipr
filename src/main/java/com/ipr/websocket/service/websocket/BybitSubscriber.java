package com.ipr.websocket.service.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.WebSocketClient;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BybitSubscriber {

    private final WebSocketClient webSocketClient;
    private final KlineServiceWebsocket klineServiceWebsocket;
    @Value("${services.bybit.symbol}")
    private final List<String> symbols;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void subscribeToSymbols() {
        connectToWebSocket(requestTopic());
    }


    private void connectToWebSocket(List<String> topics) {
        webSocketClient.execute(
                        new WebSocketClientHandler(klineServiceWebsocket, topics, objectMapper),
                        "wss://stream.bybit.com/v5/public/linear")
                .whenComplete((session, throwable) -> {
                    if (throwable != null) {
                        log.error("Ошибка при подключении к WebSocket для symbols {}: {}", symbols, throwable.getMessage());
                    } else {
                        log.info("Подключение к WebSocket успешно установлено для symbols {}", symbols);
                    }
                });
    }

    private List<String> requestTopic() {
        return symbols.stream()
                .map(symbol -> "kline.1." + symbol)
                .toList();
    }
}
