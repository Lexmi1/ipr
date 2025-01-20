package com.ipr.input;

import com.ipr.dto.Kline;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaListeners {

    @KafkaListener(topics = "${spring.kafka.topic}", containerFactory = "strategyKlineRequestFactory")
    void listener(Kline newKline) {
        log.info("Получили сообщение. Последня закрытая свеча: - symbol: {}, openPrice: {}, closePrice: {}, highPrice: {}, lowPrice: {}",
                newKline.getSymbol(), newKline.getOpen(), newKline.getClose(), newKline.getHigh(), newKline.getLow());
    }
}
