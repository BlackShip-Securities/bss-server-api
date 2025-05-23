package com.bss.bssserverapi.global.websocket.binance.client;

import com.bss.bssserverapi.global.batch.kline.event.KlineJobTriggerEvent;
import com.bss.bssserverapi.global.websocket.binance.BinanceRedisTopicType;
import com.bss.bssserverapi.global.websocket.binance.dto.BinanceMessage;
import com.bss.bssserverapi.global.websocket.binance.handler.BinanceMessageDispatcher;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
public class BinanceWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final Runnable reconnect;
    private final BinanceMessageDispatcher dispatcher;
    private final ApplicationEventPublisher eventPublisher;

    public BinanceWebSocketHandler(
            final ObjectMapper objectMapper,
            final Runnable reconnect,
            final BinanceMessageDispatcher dispatcher,
            final ApplicationEventPublisher eventPublisher
    ) {

        this.objectMapper = objectMapper;
        this.reconnect = reconnect;
        this.dispatcher = dispatcher;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) throws Exception {

        log.info("[Binance] WebSocket connection established");

        // TODO: kline batch
//        eventPublisher.publishEvent(new KlineJobTriggerEvent(this));
    }

    @Override
    protected void handleTextMessage(final WebSocketSession session, final TextMessage message) throws Exception {

        try {
            this.processMessage(message.getPayload());
        } catch (IllegalArgumentException e){
//            log.error("[Binance] Error unsupported event type", e);
//            reconnect.run();
        } catch (Exception e) {
//            log.error("[Binance] Error parsing message", e);
//            reconnect.run();
        }
    }

    private void processMessage(final String payload) throws Exception{

        JsonNode root = this.objectMapper.readTree(payload);
        if(!root.has("data"))
            return;

        JsonNode data = root.get("data");
        String eventType = data.get("e").asText();

        BinanceRedisTopicType topicType = BinanceRedisTopicType.fromEventType(eventType)
                .orElseThrow(() -> new IllegalArgumentException("Unsupported event type: " + eventType));

        BinanceMessage binanceMessage = (BinanceMessage) this.objectMapper.treeToValue(data, topicType.getMessageType());

        // DB or Redis save
        this.dispatcher.dispatch(topicType, binanceMessage);
    }

    @Override
    public void handleTransportError(final WebSocketSession session, final Throwable exception) throws Exception {

        log.error("[Binance] Transport error", exception);
        // TODO: afterConnectionClosed와 중복되지 않도록
//        reconnect.run();
    }

    @Override
    public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) throws Exception {

        log.info("[Binance] Connection closed: {}", status);
        // TODO: status에 따라 분기
//        reconnect.run();
    }
}
