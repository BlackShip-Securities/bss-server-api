package com.bss.bssserverapi.global.websocket.client;

import com.bss.bssserverapi.global.websocket.RedisTopicType;
import com.bss.bssserverapi.global.websocket.dto.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
public class BinanceWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final Runnable reconnect;
    private final RedissonClient redissonClient;


    public BinanceWebSocketHandler(final ObjectMapper objectMapper, final Runnable reconnect, final RedissonClient redissonClient) {

        this.objectMapper = objectMapper;
        this.reconnect = reconnect;
        this.redissonClient = redissonClient;
    }

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) throws Exception {

        log.info("[Binance] WebSocket connection established");
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

        RedisTopicType topicType = RedisTopicType.fromStreamName(eventType)
                .orElseThrow(() -> new IllegalArgumentException("Unsupported event type: " + eventType));

        BinanceMessage binanceMessage = (BinanceMessage) this.objectMapper.treeToValue(data, topicType.getMessageType());

        String redisTopic = topicType.getRedisPrefix() + binanceMessage.getSymbol().toLowerCase();
        this.redissonClient.getTopic(redisTopic).publish(binanceMessage);
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
