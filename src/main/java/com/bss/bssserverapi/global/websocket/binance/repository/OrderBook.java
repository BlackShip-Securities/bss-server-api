package com.bss.bssserverapi.global.websocket.binance.repository;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.NavigableMap;
import java.util.TreeMap;

@Getter
public class OrderBook {

    private String symbol;
    private Long lastUpdateId;

    private final NavigableMap<BigDecimal, BigDecimal> bids = new TreeMap<>((a, b) -> b.compareTo(a)); // 내림차순
    private final NavigableMap<BigDecimal, BigDecimal> asks = new TreeMap<>(); // 오름차순


    public OrderBook(final String symbol) {

        this.lastUpdateId = 0L;
        this.symbol = symbol;
    }

    public void clear(){

        lastUpdateId = 0L;
        bids.clear();
        asks.clear();
    }

    public void updateBid(final BigDecimal price, final BigDecimal quantity) {

        if (quantity.compareTo(BigDecimal.ZERO) == 0) {
            bids.remove(price);
        } else {
            bids.put(price, quantity);
        }
    }

    public void updateAsk(final BigDecimal price, final BigDecimal quantity) {

        if (quantity.compareTo(BigDecimal.ZERO) == 0) {
            asks.remove(price);
        } else {
            asks.put(price, quantity);
        }
    }

    public void setLastUpdateId(final Long updateId) {

        this.lastUpdateId = updateId;
    }
}