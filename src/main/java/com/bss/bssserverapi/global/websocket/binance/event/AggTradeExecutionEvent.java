package com.bss.bssserverapi.global.websocket.binance.event;

import java.math.BigDecimal;

public record AggTradeExecutionEvent(
        String symbol,
        BigDecimal price,
        BigDecimal totalQty
) {}
