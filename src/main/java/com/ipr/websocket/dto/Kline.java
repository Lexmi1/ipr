package com.ipr.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Модель свечи.
 */
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Kline {

    private String symbol;

    private double open;

    private double close;

    private double high;

    private double low;

    private double volume;
}
