package com.bss.bssserverapi.global.websocket.binance.repository;


import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryOrderBookRepository {

    private final Map<String, OrderBook> bookMap = new ConcurrentHashMap<>();

    public OrderBook findOrSaveBySymbol(final String symbol) {

        return bookMap.computeIfAbsent(symbol, s -> new OrderBook(symbol));
    }

    public void removeBySymbol(final String symbol) {

        bookMap.remove(symbol);
    }
}