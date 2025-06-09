package com.bss.bssserverapi.global.websocket.binance;

import com.bss.bssserverapi.global.websocket.binance.dto.*;
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
    DEPTH("depthUpdate", "crypto/depth/", BinanceDepthMessage.class),
    AGG_TRADE("aggTrade", "crypto/aggTrade",BinanceAggTradeMessage.class);

    private final String eventType;
    private final String redisPrefix;
    private final Class<?> messageType;

    public static Optional<BinanceRedisTopicType> fromEventType(final String eventType){

        return Arrays.stream(values())
                .filter(t -> t.eventType.equals(eventType))
                .findFirst();
    }
}
