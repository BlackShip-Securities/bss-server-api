package com.bss.bssserverapi.global.websocket.binance.handler;

import com.bss.bssserverapi.global.websocket.binance.BinanceRedisTopicType;
import com.bss.bssserverapi.global.websocket.binance.dto.BinanceMessage;

public interface BinanceMessageHandler<T extends BinanceMessage> {

    void handle(final BinanceRedisTopicType topicType, final T message);
    Class<T> getMessageType();
}
