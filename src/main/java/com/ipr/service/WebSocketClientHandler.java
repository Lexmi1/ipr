package com.ipr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipr.dto.KlineWebSocketMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
class WebSocketClientHandler extends TextWebSocketHandler {
    private final ProcessKlineService klineService;
    private final List<String> topics;
    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("WebSocket соединение установлено");

        try {
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
        } else if (payload.contains("kline")) {
            klineService.processKlineMessage(payload);
        } else {
            log.info("Получено сообщение от Bybit: {}", payload);
        }
    }
}
