package com.bss.bssserverapi.global.websocket.server;

import com.bss.bssserverapi.global.websocket.dto.TickerMessage;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisPriceListener {

    private final RedissonClient redissonClient;
    private final SimpMessagingTemplate messagingTemplate;

    @PostConstruct
    public void register() {
        List<String> symbols = List.of("btcusdt", "ethusdt");
        for(String symbol : symbols) {
            String topic = "crypto/price/" + symbol;
            redissonClient.getTopic(topic)
                    .addListener(TickerMessage.class, (channel, message) ->  {
                        messagingTemplate.convertAndSend("/topic/crypto/price/" + symbol, message);
                    });
        }
    }
}
