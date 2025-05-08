package com.bss.bssserverapi.global.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private WebSocketClient client;
    private WebSocketSession session;
    private BinanceWebSocketHandler handler;

    private final ObjectMapper objectMapper;
//    private final List<String> symbols = List.of("btcusdt", "ethusdt", "soluusdt");
    private final List<String> symbols = List.of("btcusdt", "ethusdt");

    @PostConstruct
    public void init() {

        this.handler = new BinanceWebSocketHandler(objectMapper, this::reconnect);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void connect() {

        try {
            client = new StandardWebSocketClient();
            String streamPath = String.join("/", symbols.stream().map(s -> s + "@ticker").toList());
            String url = BASE_BINANCE_WS_URL + streamPath;

            session = client.doHandshake(handler, new WebSocketHttpHeaders(), URI.create(url)).get();
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

        if(session != null && session.isOpen()){
            try {
                log.info("Disconnected to Binance WebSocket: {}", BASE_BINANCE_WS_URL);
                session.close();
            } catch (IOException e) {
                log.error("Error closing websocket", e);
            }
        }
    }
}
