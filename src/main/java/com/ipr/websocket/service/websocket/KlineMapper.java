package com.ipr.websocket.service.websocket;

import com.bybit.api.client.domain.websocket_message.public_channel.KlineData;
import com.ipr.websocket.dto.Kline;
import com.ipr.websocket.dto.KlineId;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;

@Service
public class KlineMapper {

    public Kline mapToKline(String symbol, KlineData kline) {
        return Kline.builder()
                .klineId(KlineId.builder()
                        .startTime(Instant.ofEpochMilli(kline.getStart()).atZone(ZoneOffset.UTC).toLocalDateTime())
                        .symbol(symbol)
                        .build())
                .open(Double.parseDouble(kline.getOpen()))
                .close(Double.parseDouble(kline.getClose()))
                .low(Double.parseDouble(kline.getLow()))
                .high(Double.parseDouble(kline.getHigh()))
                .volume(Double.parseDouble(kline.getVolume()))
                .build();
    }
}
