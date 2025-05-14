package com.bss.bssserverapi.global.websocket.binance.handler;

import com.bss.bssserverapi.global.websocket.binance.dto.BinanceMessage;

public interface BinanceMessageHandler<T extends BinanceMessage> {

    void handle(T message);
    Class<T> getMessageType();
}
