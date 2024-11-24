package com.ipr.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Модель свечи.
 */
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Kline {

    private KlineId klineId;

    private double open;

    private double close;

    private double high;

    private double low;

    private double volume;
}
