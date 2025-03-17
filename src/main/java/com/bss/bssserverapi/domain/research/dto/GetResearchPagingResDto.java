package com.bss.bssserverapi.domain.research.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class GetResearchPagingResDto {

    private List<GetResearchPreviewResDto> getResearchPreviewResDtoList;
    private final boolean hasNext;

    @Builder
    public GetResearchPagingResDto(final boolean hasNext, final List<GetResearchPreviewResDto> getResearchPreviewResDtoList) {

        this.hasNext = hasNext;
        this.getResearchPreviewResDtoList = getResearchPreviewResDtoList;
    }
}
