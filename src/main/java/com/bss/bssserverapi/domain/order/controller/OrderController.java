package com.bss.bssserverapi.domain.order.controller;

import com.bss.bssserverapi.domain.order.dto.CreateSpotOrderByLimitReqDto;
import com.bss.bssserverapi.domain.order.dto.CreateSpotOrderByMarketReqDto;
import com.bss.bssserverapi.domain.order.service.OrderByLimitService;
import com.bss.bssserverapi.domain.order.service.OrderByMarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderByMarketService orderByMarketService;
    private final OrderByLimitService orderByLimitService;

    @PostMapping("/market")
    public ResponseEntity<?> createSpotOrderByMarket(@AuthenticationPrincipal String userName, @RequestBody CreateSpotOrderByMarketReqDto createSpotOrderByMarketReqDto) {

        orderByMarketService.createSpotOrderByMarket(userName, createSpotOrderByMarketReqDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("");
    }

    @PostMapping("/limit")
    public ResponseEntity<?> createSpotOrderByLimit(@AuthenticationPrincipal String userName, @RequestBody CreateSpotOrderByLimitReqDto createSpotOrderByLimitReqDto) {

        orderByLimitService.createSpotOrderByLimit(userName, createSpotOrderByLimitReqDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("");
    }
}
