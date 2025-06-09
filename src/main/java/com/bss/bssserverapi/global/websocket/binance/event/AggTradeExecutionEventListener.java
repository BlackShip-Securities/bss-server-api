package com.bss.bssserverapi.global.websocket.binance.event;

import com.bss.bssserverapi.global.websocket.binance.service.OrderMatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AggTradeExecutionEventListener {

    private final OrderMatchingService orderMatchingService;

    @Async
    @EventListener
    public void onEvent(final AggTradeExecutionEvent event) {

        orderMatchingService.executeMatching(event.symbol(), event.price(), event.totalQty());
    }
}
