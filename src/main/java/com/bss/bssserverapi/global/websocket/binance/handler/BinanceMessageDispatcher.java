package com.bss.bssserverapi.global.websocket.binance.handler;

import com.bss.bssserverapi.global.websocket.binance.dto.BinanceMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BinanceMessageDispatcher {

    private final BinanceMessageHandlerRegistry registry;

    @SuppressWarnings("unchecked")
    public <T extends BinanceMessage> void dispatch(final T message){

        BinanceMessageHandler<T> handler = (BinanceMessageHandler<T>) registry.getHandler(message.getClass());
        handler.handle(message);
    }
}
