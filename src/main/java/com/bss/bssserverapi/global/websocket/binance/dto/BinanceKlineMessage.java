package com.bss.bssserverapi.global.websocket.binance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BinanceKlineMessage implements BinanceMessage{

    @JsonProperty("e")
    private String eventType;

    @JsonProperty("E")
    private Long eventTime;

    @JsonProperty("s")
    private String symbol;

    @JsonProperty("k")
    private KlineData kline;

    @Getter
    @NoArgsConstructor
    public static class KlineData {

        @JsonProperty("t")
        private Long startTime;

        @JsonProperty("T")
        private Long closeTime;

        @JsonProperty("s")
        private String symbol;

        @JsonProperty("i")
        private String interval;

        @JsonProperty("f")
        private Long firstTradeId;

        @JsonProperty("L")
        private Long lastTradeId;

        @JsonProperty("o")
        private String openPrice;

        @JsonProperty("c")
        private String closePrice;

        @JsonProperty("h")
        private String highPrice;

        @JsonProperty("l")
        private String lowPrice;

        @JsonProperty("v")
        private String baseVolume;

        @JsonProperty("n")
        private Long tradeCount;

        @JsonProperty("x")
        private boolean isClosed;

        @JsonProperty("q")
        private String quoteVolume;

        @JsonProperty("V")
        private String takerBuyBaseVolume;

        @JsonProperty("Q")
        private String takerBuyQuoteVolume;
    }
}