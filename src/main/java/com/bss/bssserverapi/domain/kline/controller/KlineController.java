package com.bss.bssserverapi.domain.kline.controller;

import com.bss.bssserverapi.domain.kline.dto.GetKlinePagingResDto;
import com.bss.bssserverapi.domain.kline.service.KlineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/crypto/klines")
public class KlineController {

    private final KlineService klineService;

    @GetMapping
    public ResponseEntity<GetKlinePagingResDto> getKlinePaging(
            @RequestParam(value = "symbol", defaultValue = "BTCUSDT") final String symbol,
            @RequestParam(value = "interval", defaultValue = "1m") final String interval,
            @RequestParam(value = "limit", defaultValue = "100") final Long limit,
            @RequestParam(value = "lastOpenTime", defaultValue = "0") final Long lastOpenTime) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(klineService.getKlinePaging(symbol, interval, limit, lastOpenTime));
    }
}
