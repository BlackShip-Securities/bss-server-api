package com.bss.bssserverapi.domain.kline.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class GetKlinePagingResDto {

    private final List<GetKlineResDto> getKlineResDtoList;
    private final Boolean hasNext;

    @Builder
    public GetKlinePagingResDto(final List<GetKlineResDto> getKlineResDtoList, final Boolean hasNext) {

        this.getKlineResDtoList = getKlineResDtoList;
        this.hasNext = hasNext;
    }
}


