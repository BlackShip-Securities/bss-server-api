package com.bss.bssserverapi.domain.stock;

import com.bss.bssserverapi.domain.stock.dto.GetStockResDto;
import com.bss.bssserverapi.domain.stock.repository.StockRepository;
import com.bss.bssserverapi.domain.stock.service.StockService;
import com.bss.bssserverapi.global.exception.ErrorCode;
import com.bss.bssserverapi.global.exception.GlobalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private StockService stockService;

    private Stock stock;

    @BeforeEach
    void setUp() {
        stock = Stock.builder()
                .stockCode("005930")
                .name("삼성전자")
                .marketCap(5000000000000L)
                .build();
    }

    @Test
    @DisplayName("주식 단일 조회(주식 코드로) 성공")
    void getStockByStockCode_Success() {

        // given
        String stockCode = "005930";
        when(stockRepository.findStockByStockCode(stockCode)).thenReturn(Optional.of(stock));

        // when
        GetStockResDto res = stockService.getStockByStockCode(stockCode);

        // then
        assertThat(res.getStockCode()).isEqualTo("005930");
        assertThat(res.getName()).isEqualTo("삼성전자");
        assertThat(res.getMarketCap()).isEqualTo(5000000000000L);

        verify(stockRepository, times(1)).findStockByStockCode("005930");
    }

    @Test
    @DisplayName("주식 단일 조회(주식 코드로) 실패 - 존재하지 않음")
    void getStockByStockCode_ThrowsException() {

        // given
        String stockCode = "@@@@@@";
        when(stockRepository.findStockByStockCode(stockCode)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> stockService.getStockByStockCode(stockCode))
                .isInstanceOf(GlobalException.class)
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.STOCK_NOT_FOUND);

        verify(stockRepository, times(1)).findStockByStockCode(stockCode);
    }
}