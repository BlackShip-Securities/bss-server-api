package com.bss.bssserverapi.domain.stock.service;

import com.bss.bssserverapi.domain.stock.Stock;
import com.bss.bssserverapi.domain.stock.dto.GetStockResDto;
import com.bss.bssserverapi.domain.stock.repository.StockRepository;
import com.bss.bssserverapi.global.exception.ErrorCode;
import com.bss.bssserverapi.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockService {

    private final StockRepository stockRepository;

    public GetStockResDto getStockByStockCode(final String stockCode){

        Stock stock = stockRepository.findStockByStockCode(stockCode)
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, ErrorCode.STOCK_NOT_FOUND));

        return GetStockResDto.builder()
                .stockCode(stock.getStockCode())
                .name(stock.getName())
                .marketCap(stock.getMarketCap())
                .build();
    }

    public GetStockResDto getStockByName(final String name){

        Stock stock = stockRepository.findStockByName(name)
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, ErrorCode.STOCK_NOT_FOUND));

        return GetStockResDto.builder()
                .stockCode(stock.getStockCode())
                .name(stock.getName())
                .marketCap(stock.getMarketCap())
                .build();
    }
}
