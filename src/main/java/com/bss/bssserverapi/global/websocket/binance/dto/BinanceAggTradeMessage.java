package com.bss.bssserverapi.global.websocket.binance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class BinanceAggTradeMessage implements BinanceMessage {

    @JsonProperty("e")
    private String eventType;

    @JsonProperty("E")
    private Long eventTime;

    @JsonProperty("s")
    private String symbol;

    @JsonProperty("p")
    private BigDecimal price;

    @JsonProperty("q")
    private BigDecimal quantity;

    @JsonProperty("T")
    private Long tradeTime;
}