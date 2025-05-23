package com.bss.bssserverapi.global.websocket.binance;

import com.bss.bssserverapi.global.websocket.binance.dto.BinanceDepthMessage;
import com.bss.bssserverapi.global.websocket.binance.dto.BinanceKlineMessage;
import com.bss.bssserverapi.global.websocket.binance.dto.BinanceTickerMessage;
import com.bss.bssserverapi.global.websocket.binance.dto.BinanceTradeMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum BinanceRedisTopicType {

    KLINE("kline", "crypto/kline/", BinanceKlineMessage.class),
    TICKER("24hrTicker", "crypto/ticker/", BinanceTickerMessage.class),
    TRADE("trade", "crypto/trade/", BinanceTradeMessage.class),
    DEPTH("depthUpdate", "crypto/depth/", BinanceDepthMessage.class);

    private final String eventType;
    private final String redisPrefix;
    private final Class<?> messageType;

    public static Optional<BinanceRedisTopicType> fromEventType(final String eventType){

        return Arrays.stream(values())
                .filter(t -> t.eventType.equals(eventType))
                .findFirst();
    }
}
