package com.bss.bssserverapi.domain.research.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class GetResearchPagingResDto {

    private List<GetResearchPreviewResDto> getResearchPreviewResDtoList;
    private final Boolean hasNext;

    @Builder
    public GetResearchPagingResDto(final List<GetResearchPreviewResDto> getResearchPreviewResDtoList, final Boolean hasNext) {

        this.getResearchPreviewResDtoList = getResearchPreviewResDtoList;
        this.hasNext = hasNext;
    }
}
