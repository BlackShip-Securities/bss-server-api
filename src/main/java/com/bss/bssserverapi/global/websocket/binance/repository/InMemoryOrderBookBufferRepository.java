package com.bss.bssserverapi.global.websocket.binance.repository;

import com.bss.bssserverapi.global.websocket.binance.dto.BinanceDepthMessage;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InMemoryOrderBookBufferRepository {

    // symbol -> queue
    private final Map<String, Queue<BinanceDepthMessage>> bufferMap = new HashMap<>();

    public void add(final BinanceDepthMessage message) {

        final String symbol = message.getSymbol().toLowerCase();

        synchronized (bufferMap) {
            bufferMap.computeIfAbsent(symbol, k -> new LinkedList<>()).offer(message);
        }
    }

    public BinanceDepthMessage pollBySymbol(final String symbol) {

        synchronized (bufferMap) {
            Queue<BinanceDepthMessage> queue = bufferMap.get(symbol.toLowerCase());
            return queue == null ? null : queue.poll();
        }
    }

    public boolean isEmptyBySymbol(final String symbol) {

        synchronized (bufferMap) {
            Queue<BinanceDepthMessage> queue = bufferMap.get(symbol.toLowerCase());
            return queue == null || queue.isEmpty();
        }
    }

    public Set<String> getSymbols() {

        synchronized (bufferMap) {
            return new HashSet<>(bufferMap.keySet());
        }
    }
}