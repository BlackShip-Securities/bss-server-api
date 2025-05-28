package com.bss.bssserverapi.global.websocket.binance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BinanceDepthMessage implements BinanceMessage {

    @JsonProperty("e")
    private String eventType;

    @JsonProperty("E")
    private Long eventTime;

    @JsonProperty("s")
    private String symbol;

    @JsonProperty("U")
    private Long firstUpdateId;

    @JsonProperty("u")
    private Long finalUpdateId;

    @JsonProperty("b")
    private List<List<String>> bids;  // [ [price, quantity], ... ]

    @JsonProperty("a")
    private List<List<String>> asks;  // [ [price, quantity], ... ]
}