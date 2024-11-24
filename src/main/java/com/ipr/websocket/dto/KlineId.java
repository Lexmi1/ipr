package com.ipr.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Id для сущности {@link Kline}.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KlineId implements Serializable {
    private String symbol;

    private LocalDateTime startTime;
}
