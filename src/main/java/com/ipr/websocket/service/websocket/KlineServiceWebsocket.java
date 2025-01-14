package com.ipr.websocket.service.websocket;

import com.bybit.api.client.domain.websocket_message.public_channel.WebSocketKlineMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipr.websocket.dto.Kline;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class KlineServiceWebsocket {
    private final ObjectMapper objectMapper;
    private final KlineMapper klineMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    @Value("${spring.kafka.topic}")
    private String kafkaTopic;

    @SneakyThrows
    public void processKlineMessage(String message) {
        WebSocketKlineMessage webSocketKlineMessage = objectMapper.readValue(message, WebSocketKlineMessage.class);

        if (isValidOrderMessage(webSocketKlineMessage)) {
            String topic = webSocketKlineMessage.getTopic();
            String symbol = topic.split("\\.")[2];

            webSocketKlineMessage.getData().forEach(klineData -> {
                if (klineData.getConfirm()) {
                    Kline newKline = klineMapper.mapToKline(symbol, klineData);
                    log.info("С WebSocket пришла закрытая свеча - symbol: {}, startTime: {}, openPrice: {}, closePrice: {}, highPrice: {}, lowPrice: {}",
                            newKline.getKlineId().getSymbol(), newKline.getKlineId().getStartTime(), newKline.getOpen(), newKline.getClose(), newKline.getHigh(), newKline.getLow());

                    // Отправляем цену последней свечи
                    Message<String> kafkaMessage = MessageBuilder
                            .withPayload(String.valueOf(newKline.getClose()))
                            .setHeader(KafkaHeaders.TOPIC, kafkaTopic)
                            .build();
                    kafkaTemplate.send(kafkaMessage);
                }
            });
        } else {
            log.error("Сообщение, полученное от Bybit невалидно: {}", message);
        }
    }

    private boolean isValidOrderMessage(WebSocketKlineMessage webSocketKlineMessage) {
        return Objects.nonNull(webSocketKlineMessage.getData()) && !webSocketKlineMessage.getData().isEmpty();
    }
}
