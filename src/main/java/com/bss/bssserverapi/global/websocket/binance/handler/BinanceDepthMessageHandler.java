package com.bss.bssserverapi.global.websocket.binance.handler;

import com.bss.bssserverapi.global.websocket.binance.BinanceRedisTopicType;
import com.bss.bssserverapi.global.websocket.binance.dto.BinanceDepthMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BinanceDepthMessageHandler implements BinanceMessageHandler<BinanceDepthMessage> {

    @Override
    public void handle(final BinanceRedisTopicType topicType, final BinanceDepthMessage message) {

        log.info("Binance Depth Update | EventType: {}, EventTime: {}, Symbol: {}, FirstUpdateID: {}, FinalUpdateID: {}",
                message.getEventType(), message.getEventTime(), message.getSymbol(),
                message.getFirstUpdateId(), message.getFinalUpdateId());

        log.info("--- Bids ---");
        message.getBids().forEach(bid ->
                log.info("Price: {}, Quantity: {}", bid.get(0), bid.get(1))
        );

        log.info("--- Asks ---");
        message.getAsks().forEach(ask ->
                log.info("Price: {}, Quantity: {}", ask.get(0), ask.get(1))
        );
    }

    @Override
    public Class<BinanceDepthMessage> getMessageType() {

        return BinanceDepthMessage.class;
    }
}