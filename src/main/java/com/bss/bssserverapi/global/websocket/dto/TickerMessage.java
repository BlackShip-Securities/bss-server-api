package com.bss.bssserverapi.global.websocket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TickerMessage {

    @JsonProperty("e")
    private String eventType;

    @JsonProperty("E")
    private Long eventTime;

    @JsonProperty("s")
    private String symbol;

    @JsonProperty("p")
    private String priceChange;

    @JsonProperty("P")
    private String priceChangePercent;

    @JsonProperty("w")
    private String weightedAvgPrice;

    @JsonProperty("x")
    private String prevClosePrice;

    @JsonProperty("c")
    private String lastPrice;

    @JsonProperty("Q")
    private String lastQuantity;

    @JsonProperty("b")
    private String bestBidPrice;

    @JsonProperty("B")
    private String bestBidQty;

    @JsonProperty("a")
    private String bestAskPrice;

    @JsonProperty("A")
    private String bestAskQty;

    @JsonProperty("o")
    private String openPrice;

    @JsonProperty("h")
    private String highPrice;

    @JsonProperty("l")
    private String lowPrice;

    @JsonProperty("v")
    private String volumeBase;

    @JsonProperty("q")
    private String volumeQuote;

    @JsonProperty("O")
    private Long openTime;

    @JsonProperty("C")
    private Long closeTime;

    @JsonProperty("F")
    private Long firstTradeId;

    @JsonProperty("L")
    private Long lastTradeId;

    @JsonProperty("n")
    private Long totalTrades;
}