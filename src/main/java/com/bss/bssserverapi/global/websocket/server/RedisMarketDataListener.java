package com.bss.bssserverapi.global.websocket.server;

import com.bss.bssserverapi.global.websocket.RedisTopicType;
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
public class RedisMarketDataListener {

    private final RedissonClient redissonClient;
    private final SimpMessagingTemplate messagingTemplate;

    @PostConstruct
    public void register() {

        List<String> symbols = List.of("btcusdt", "ethusdt");

        for(RedisTopicType type : RedisTopicType.values()) {
            for(String symbol : symbols) {
                String redisTopic = type.getRedisPrefix() + symbol;
                redissonClient.getTopic(redisTopic)
                        .addListener(Object.class, (channel, message) ->  {
                            messagingTemplate.convertAndSend("/topic/" + redisTopic, message);
                        });
            }
        }
    }
}
