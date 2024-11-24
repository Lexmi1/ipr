package com.ipr.websocket.service.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.WebSocketClient;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class BybitSubscriber {

    private final WebSocketClient webSocketClient;
    private final KlineServiceWebsocket klineServiceWebsocket;
    @Value("${services.bybit.symbol}")
    private final List<String> symbols;
    private final ObjectMapper objectMapper;
    private int reconnectAttempt = 0;
    @Value("${services.bybit.reconnect.delay:5000}")
    private long reconnectDelay;
    @Value("${services.bybit.reconnect.maxAttempts:1000}")
    private int maxReconnectAttempts;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    public void subscribeToSymbols() {
        connectToWebSocket(requestTopic());
    }

    public void reconnectToSymbols() {
        reconnectAttempt++;
        if (reconnectAttempt > maxReconnectAttempts) {
            log.error("Превышено максимальное количество попыток переподключения ({})", maxReconnectAttempts);
            return;
        }

        log.info("Попытка переподключения №{} через {} мс", reconnectAttempt, reconnectDelay);

        scheduler.schedule(() -> {
            connectToWebSocket(requestTopic());
        }, reconnectDelay, TimeUnit.MILLISECONDS);
    }

    private void connectToWebSocket(List<String> topics) {
        webSocketClient.execute(
                        new WebSocketClientHandler(this, klineServiceWebsocket, topics, objectMapper),
                        "wss://stream.bybit.com/v5/public/linear")
                .whenComplete((session, throwable) -> {
                    if (throwable != null) {
                        log.error("Ошибка при подключении к WebSocket для symbols {}: {}", symbols, throwable.getMessage());
                        reconnectToSymbols(); // Повторное подключение в случае ошибки
                    } else {
                        log.info("Подключение к WebSocket успешно установлено для symbols {}", symbols);
                        reconnectAttempt = 0; // Сброс счетчика при успешном подключении
                    }
                });
    }

    private List<String> requestTopic() {
        return symbols.stream()
                .map(symbol -> "kline.1." + symbol)
                .toList();
    }
}
