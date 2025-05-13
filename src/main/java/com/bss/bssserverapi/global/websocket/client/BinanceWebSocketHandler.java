package com.bss.bssserverapi.global.websocket.client;

import com.bss.bssserverapi.global.websocket.dto.KlineMessage;
import com.bss.bssserverapi.global.websocket.dto.RedisTopicType;
import com.bss.bssserverapi.global.websocket.dto.TickerMessage;
import com.bss.bssserverapi.global.websocket.dto.TradeMessage;
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
                String eventType = data.get("e").asText();

                switch (eventType) {
                    case "24hrTicker" -> {
                        TickerMessage tickerMessage = this.objectMapper.treeToValue(data, TickerMessage.class);
                        this.redissonClient.getTopic(RedisTopicType.TICKER.getRedisPrefix() + tickerMessage.getSymbol().toLowerCase())
                                .publish(tickerMessage);
//                    log.info("[Binance - 24hrTicker] {} | Last: {} | High: {} | Low: {} | Volume: {}",
//                            tickerMessage.getSymbol(),
//                            tickerMessage.getLastPrice(),
//                            tickerMessage.getHighPrice(),
//                            tickerMessage.getLowPrice(),
//                            tickerMessage.getVolumeBase());
                    }
                    case "kline" -> {
                        KlineMessage klineMessage = this.objectMapper.treeToValue(data, KlineMessage.class);
//                    log.info("[Binance - kline] {} | o:{} h:{} l:{} c:{} x:{}",
//                            klineMessage.getSymbol(),
//                            klineMessage.getKline().getOpenPrice(),
//                            klineMessage.getKline().getHighPrice(),
//                            klineMessage.getKline().getLowPrice(),
//                            klineMessage.getKline().getClosePrice(),
//                            klineMessage.getKline().isClosed()
//                    );
                    }
                    case "trade" -> {
                        TradeMessage tradeMessage = this.objectMapper.treeToValue(data, TradeMessage.class);
//                    log.info("[Binance - trade] {} | price:{} qty:{} time:{} marketMaker:{}",
//                            tradeMessage.getSymbol(),
//                            tradeMessage.getPrice(),
//                            tradeMessage.getQuantity(),
//                            tradeMessage.getTradeTime(),
//                            tradeMessage.isBuyerMarketMaker());
                    }
                }
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
