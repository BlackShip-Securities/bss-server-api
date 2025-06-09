package com.bss.bssserverapi.global.websocket.binance.service;

import com.bss.bssserverapi.global.websocket.binance.dto.BinanceAggTradeMessage;
import com.bss.bssserverapi.global.websocket.binance.event.AggTradeExecutionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class TradeAggregator {

    private final Map<String, BigDecimal> aggregatedTrades = new ConcurrentHashMap<>();
    private final ApplicationEventPublisher eventPublisher;

    public void accumulate(final BinanceAggTradeMessage message) {

        final String key = message.getSymbol() + "|" + message.getPrice().toPlainString();
        aggregatedTrades.merge(key, message.getQuantity(), BigDecimal::add);
    }

    @Scheduled(fixedRate = 1000)
    public void flush() {

        if (aggregatedTrades.isEmpty()) return;

        for (final Map.Entry<String, BigDecimal> entry : aggregatedTrades.entrySet()) {
            final String[] parts = entry.getKey().split("\\|");
            final String symbol = parts[0];
            final BigDecimal price = new BigDecimal(parts[1]);
            final BigDecimal totalQty = entry.getValue();

            eventPublisher.publishEvent(new AggTradeExecutionEvent(symbol, price, totalQty));
        }

        aggregatedTrades.clear();
    }
}