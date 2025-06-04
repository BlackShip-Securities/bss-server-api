package com.bss.bssserverapi.domain.order.controller;

import com.bss.bssserverapi.domain.order.dto.CreateSpotOrderByMarketReqDto;
import com.bss.bssserverapi.domain.order.service.OrderService;
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

    private final OrderService orderService;

    @PostMapping("/buy/market")
    public ResponseEntity<?> createSpotOrderByMarket(@AuthenticationPrincipal String userName, @RequestBody CreateSpotOrderByMarketReqDto createSpotOrderByMarketReqDto) {

        orderService.createSpotOrderByMarket(userName, createSpotOrderByMarketReqDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("");
    }
}
