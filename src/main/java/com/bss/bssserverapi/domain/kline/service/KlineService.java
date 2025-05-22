package com.bss.bssserverapi.domain.kline.service;

import com.bss.bssserverapi.domain.crypto.Crypto;
import com.bss.bssserverapi.domain.crypto.repository.CryptoJpaRepository;
import com.bss.bssserverapi.domain.kline.Kline;
import com.bss.bssserverapi.domain.kline.dto.GetKlinePagingResDto;
import com.bss.bssserverapi.domain.kline.dto.GetKlineResDto;
import com.bss.bssserverapi.domain.kline.repository.KlineJpaRepository;
import com.bss.bssserverapi.global.exception.ErrorCode;
import com.bss.bssserverapi.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KlineService {

    private final CryptoJpaRepository cryptoJpaRepository;
    private final KlineJpaRepository klineJpaRepository;

    @Transactional(readOnly = true)
    public GetKlinePagingResDto getKlinePaging(final String symbol, final String interval, final Long limit, final Long lastOpenTime) {

        final Crypto crypto = cryptoJpaRepository.findBySymbol(symbol)
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, ErrorCode.CRYPTO_NOT_FOUND));

        final Long fetchSize = limit + 1;

        final List<Kline> klineList = (lastOpenTime == 0L)
                ? klineJpaRepository.findFirstPageBySymbolAndInterval(crypto.getSymbol(), interval, fetchSize)
                : klineJpaRepository.findNextPageBySymbolAndInterval(crypto.getSymbol(), interval, fetchSize, lastOpenTime);

        final Boolean hasNext = klineList.size() == fetchSize;
        if(hasNext){
            klineList.remove(klineList.size() - 1);
        }

        final List<GetKlineResDto> list = klineList.stream()
                .map(GetKlineResDto::toDto)
                .toList();

        return GetKlinePagingResDto.builder()
                .getKlineResDtoList(list)
                .hasNext(hasNext)
                .build();
    }
}
