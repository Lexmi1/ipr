package com.ipr.service;

import com.bybit.api.client.domain.websocket_message.public_channel.WebSocketKlineMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipr.dto.Kline;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessKlineService {
    private final ObjectMapper objectMapper;
    private final KlineMapperService klineMapperService;
    private final KafkaTemplate<String, Kline> kafkaTemplate;
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
                    Kline newKline = klineMapperService.mapToKline(symbol, klineData);
                    log.info("С WebSocket пришла закрытая свеча - symbol: {}, openPrice: {}, closePrice: {}, highPrice: {}, lowPrice: {}",
                            newKline.getSymbol(), newKline.getOpen(), newKline.getClose(), newKline.getHigh(), newKline.getLow());

                    kafkaTemplate.send(kafkaTopic, newKline);
                    log.info("Отправили сообщение в кафку");
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
