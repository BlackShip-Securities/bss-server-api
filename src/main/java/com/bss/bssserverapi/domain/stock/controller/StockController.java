package com.bss.bssserverapi.domain.stock.controller;

import com.bss.bssserverapi.domain.stock.dto.GetStockResDto;
import com.bss.bssserverapi.domain.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping("/stock-code/{stock-code}")
    public ResponseEntity<GetStockResDto> getStockByStockCode(@PathVariable("stock-code") final String stockCode){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stockService.getStockByStockCode(stockCode));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<GetStockResDto> getStockByStockName(@PathVariable("name") final String name){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stockService.getStockByName(name));
    }
}
