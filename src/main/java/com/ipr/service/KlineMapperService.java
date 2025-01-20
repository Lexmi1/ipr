package com.ipr.service;

import com.bybit.api.client.domain.websocket_message.public_channel.KlineData;
import com.ipr.dto.Kline;
import org.springframework.stereotype.Service;

@Service
public class KlineMapperService {

    public Kline mapToKline(String symbol, KlineData kline) {
        return Kline.builder()
                .symbol(symbol)
                .open(Double.parseDouble(kline.getOpen()))
                .close(Double.parseDouble(kline.getClose()))
                .low(Double.parseDouble(kline.getLow()))
                .high(Double.parseDouble(kline.getHigh()))
                .volume(Double.parseDouble(kline.getVolume()))
                .build();
    }
}
