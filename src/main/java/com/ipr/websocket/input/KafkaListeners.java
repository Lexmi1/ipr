package com.ipr.websocket.input;

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
    @KafkaListener(topics = "#{'${spring.kafka.topic}'}", groupId = "#{'${spring.kafka.groupId}'}")
    void listener(String condition) {
        log.info("Получили сообщение: {}", condition);
    }
}
