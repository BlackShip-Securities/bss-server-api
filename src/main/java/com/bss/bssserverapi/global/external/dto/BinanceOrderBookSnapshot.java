package com.bss.bssserverapi.global.external.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class BinanceOrderBookSnapshot {

    @JsonProperty("lastUpdateId")
    private Long lastUpdateId;

    @JsonProperty("bids")
    private List<List<String>> bids;  // [price, qty]

    @JsonProperty("asks")
    private List<List<String>> asks;  // [price, qty]
}