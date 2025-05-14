package com.bss.bssserverapi.global.websocket.binance.handler;

import com.bss.bssserverapi.global.websocket.binance.dto.BinanceMessage;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BinanceMessageHandlerRegistry {

    private final Map<Class<?>, BinanceMessageHandler<?>> handlerMap = new HashMap<>();

    public BinanceMessageHandlerRegistry(final List<BinanceMessageHandler<?>> handlers) {

        for(BinanceMessageHandler<?> handler : handlers){
            handlerMap.put(handler.getMessageType(), handler);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends BinanceMessage> BinanceMessageHandler<T> getHandler(final Class<T> messageType) {

        BinanceMessageHandler<T> handler = (BinanceMessageHandler<T>) handlerMap.get(messageType);
        if (handler == null) {
            throw new IllegalArgumentException("No handler found for type: " + messageType.getName());
        }
        return handler;
    }
}
