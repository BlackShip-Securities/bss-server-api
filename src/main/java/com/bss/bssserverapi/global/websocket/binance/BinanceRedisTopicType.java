package com.bss.bssserverapi.global.websocket.binance;

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
    TRADE("trade", "crypto/trade/", BinanceTradeMessage.class);

    private final String streamName;
    private final String redisPrefix;
    private final Class<?> messageType;

    public static Optional<BinanceRedisTopicType> fromStreamName(final String streamName){

        return Arrays.stream(values())
                .filter(t -> t.streamName.equals(streamName))
                .findFirst();
    }
}
