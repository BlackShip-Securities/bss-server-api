package com.bss.bssserverapi.domain.order.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    @PostMapping("/buy/market")
    public ResponseEntity<?> createBuyOrderByMarket() {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("");
    }
}
