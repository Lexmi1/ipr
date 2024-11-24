package com.ipr.websocket.service.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipr.websocket.dto.KlineWebSocketMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@RequiredArgsConstructor
@Slf4j
class WebSocketClientHandler extends TextWebSocketHandler {
    private final BybitSubscriber bybitSubscriber;
    private final KlineServiceWebsocket klineService;
    private final List<String> topics;
    private final ObjectMapper objectMapper;
    private AtomicLong lastPongTime = new AtomicLong(System.currentTimeMillis());

    private ScheduledExecutorService pingScheduler = Executors.newSingleThreadScheduledExecutor();
    private WebSocketSession currentSession;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("WebSocket соединение установлено");
        this.currentSession = session;
        startPingTask();

        try {
            // Отправка подписки на топики
            KlineWebSocketMessage message = new KlineWebSocketMessage("subscribe", topics);
            String jsonMessage = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(jsonMessage));
            log.info("Отправлено сообщение подписки: {}", jsonMessage);
        } catch (IOException e) {
            log.error("Ошибка при отправке сообщения подписки: {}", e.getMessage());
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        if (payload.contains("\"op\":\"pong\"")) {
            log.debug("Получен pong от сервера");
            // Обновляем время получения последнего pong
            lastPongTime.set(System.currentTimeMillis());
        } else if (payload.contains("kline")) {
            klineService.processKlineMessage(payload);
        } else {
            log.info("Получено сообщение от Bybit: {}", payload);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("Ошибка в WebSocket соединении: {}", exception.getMessage(), exception);
        session.close();
        stopPingTask();
        bybitSubscriber.reconnectToSymbols();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws IOException {
        log.info("WebSocket соединение закрыто, статус: {}", status);
        session.close();
        stopPingTask();
        bybitSubscriber.reconnectToSymbols();
    }

    private void startPingTask() {
        pingScheduler.scheduleAtFixedRate(() -> {
            sendPing();
            checkPongResponse();
        }, 20, 20, TimeUnit.SECONDS);
    }

    private void checkPongResponse() {
        long currentTime = System.currentTimeMillis();
        long lastPong = lastPongTime.get();
        long timeSinceLastPong = currentTime - lastPong;

        if (timeSinceLastPong > 30000) { // 30 секунд без pong
            log.warn("Более 30 секунд без ответа pong от сервера. Переподключение...");
            try {
                currentSession.close();
            } catch (IOException e) {
                log.error("Ошибка при закрытии сессии: {}", e.getMessage());
            }
            stopPingTask();
            bybitSubscriber.reconnectToSymbols();
        }
    }

    private void stopPingTask() {
        pingScheduler.shutdownNow();
    }

    private void sendPing() {
        if (currentSession != null && currentSession.isOpen()) {
            try {
                String pingMessage = "{\"req_id\": \"100001\", \"op\": \"ping\"}";
                currentSession.sendMessage(new TextMessage(pingMessage));
                log.info("Отправлено ping сообщение");
                // Обновляем время отправки пинга
                lastPongTime.set(System.currentTimeMillis());
            } catch (IOException e) {
                log.error("Ошибка при отправке ping сообщения: {}", e.getMessage());
            }
        }
    }
}
