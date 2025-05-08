package com.bss.bssserverapi.global.websocket;

import com.bss.bssserverapi.global.websocket.dto.TickerMessage;
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

        log.info("Binance WebSocket connection established");
    }

    @Override
    protected void handleTextMessage(final WebSocketSession session, final TextMessage message) throws Exception {

        try {
            JsonNode root = this.objectMapper.readTree(message.getPayload());

            if (root.has("data")){
                JsonNode data = root.get("data");
                TickerMessage tickerMessage = this.objectMapper.treeToValue(data, TickerMessage.class);

                this.redissonClient.getTopic("crypto/" + tickerMessage.getSymbol())
                        .publish(tickerMessage);

                // 원하는 데이터만 출력
                log.info("[Binance] {} | Last: {} | High: {} | Low: {} | Volume: {}",
                        tickerMessage.getSymbol(),
                        tickerMessage.getLastPrice(),
                        tickerMessage.getHighPrice(),
                        tickerMessage.getLowPrice(),
                        tickerMessage.getVolumeBase()
                );
            }
        } catch (Exception e) {
            log.error("[Binance] Error parsing ticker message", e);
            reconnect.run();
        }
    }

    @Override
    public void handleTransportError(final WebSocketSession session, final Throwable exception) throws Exception {

        log.error("Transport error", exception);
        // TODO: afterConnectionClosed와 중복되지 않도록
//        reconnect.run();
    }

    @Override
    public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) throws Exception {

        log.info("Connection closed: {}", status);
        // TODO: status에 따라 분기
//        reconnect.run();
    }
}
