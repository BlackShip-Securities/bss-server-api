package com.bss.bssserverapi.global.websocket.binance.client;

import com.bss.bssserverapi.global.websocket.binance.handler.BinanceMessageDispatcher;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinanceWebSocketClient {

    private static final String BASE_BINANCE_WS_URL = "wss://stream.binance.com:9443/stream?streams=";
    private WebSocketClient webSocketClient;
    private WebSocketSession webSocketSession;
    private BinanceWebSocketHandler webSocketHandler;

    private final ObjectMapper objectMapper;
    private final RedissonClient redissonClient;
    private final List<String> symbols = List.of("btcusdt", "ethusdt");
    private final BinanceMessageDispatcher dispatcher;

    @PostConstruct
    public void init() {

        this.webSocketHandler = new BinanceWebSocketHandler(this.objectMapper, this::reconnect, this.redissonClient, this.dispatcher);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void connect() {

        try {
            this.webSocketClient = new StandardWebSocketClient();
            String streamPath = String.join("/", symbols.stream()
                    .flatMap(s -> List.of(s + "@ticker", s + "@kline_1m", s + "@trade").stream()).toList());
            String url = BASE_BINANCE_WS_URL + streamPath;

            this.webSocketSession = this.webSocketClient.doHandshake
                    (this.webSocketHandler, new WebSocketHttpHeaders(), URI.create(url)).get();
            log.info("Connected to Binance WebSocket: {}", url);
        } catch (Exception e) {
            log.error("Binance WebSocket connection failed", e);
        }
    }

    // TODO:
    public void reconnect() {

        log.info("Attempting to reconnect...");
        try {
            Thread.sleep(2000);
            this.connect();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Reconnection interrupted", e);
        }
    }

    @PreDestroy
    public void disconnect() {

        if(this.webSocketSession != null && this.webSocketSession.isOpen()){
            try {
                log.info("Disconnected to Binance WebSocket: {}", BASE_BINANCE_WS_URL);
                this.webSocketSession.close();
            } catch (IOException e) {
                log.error("Error closing websocket", e);
            }
        }
    }
}
