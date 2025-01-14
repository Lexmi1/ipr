package com.ipr.websocket.input;

import com.ipr.websocket.dto.Kline;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaListeners {

    @SneakyThrows
    @KafkaListener(topics = "${spring.kafka.topic}", containerFactory = "strategyKlineRequestFactory")
    void listener(Kline message) {
        log.info("Получили сообщение. Последня закрытая свеча: {}", message);
    }
}
