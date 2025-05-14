package com.bss.bssserverapi.global.websocket.binance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BinanceTradeMessage implements BinanceMessage{

    @JsonProperty("e")
    private String eventType;

    @JsonProperty("s")
    private String symbol;

    @JsonProperty("E")
    private Long eventTime;

    @JsonProperty("t")
    private Long tradeId;

    @JsonProperty("p")
    private String price;

    @JsonProperty("q")
    private String quantity;

    @JsonProperty("T")
    private Long tradeTime;

    @JsonProperty("m")
    private boolean isBuyerMarketMaker;

    @JsonProperty("M")
    private boolean ignore;
}