package com.bss.bssserverapi.global.batch.kline.dto;

public record KlineFetchRequest(String symbol, String interval, Long startTime, Long endTime) { }
