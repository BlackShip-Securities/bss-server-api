package com.bss.bssserverapi.global.websocket.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisTopicType {

    TICKER("crypto/ticker/", "/topic/crypto/ticker/");

    private final String redisPrefix;
    private final String wsPrefix;
}
