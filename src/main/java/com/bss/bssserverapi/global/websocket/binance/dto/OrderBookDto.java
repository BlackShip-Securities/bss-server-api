package com.bss.bssserverapi.global.websocket.binance.dto;

import com.bss.bssserverapi.global.websocket.binance.repository.OrderBook;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderBookDto {

    private String symbol;
    private Long lastUpdateId;
    private List<List<String>> asks;
    private List<List<String>> bids;

    public static OrderBookDto from(final OrderBook orderBook, final Long depth) {

        final List<List<String>> asks = orderBook.getAsks().entrySet().stream()
                .limit(depth)
                .map(e -> List.of(e.getKey().toPlainString(), e.getValue().toPlainString()))
                .toList();

        final List<List<String>> bids = orderBook.getBids().entrySet().stream()
                .limit(depth)
                .map(e -> List.of(e.getKey().toPlainString(), e.getValue().toPlainString()))
                .toList();

        return new OrderBookDto(orderBook.getSymbol(), orderBook.getLastUpdateId(), asks, bids);
    }
}