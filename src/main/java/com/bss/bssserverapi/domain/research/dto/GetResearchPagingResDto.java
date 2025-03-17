package com.bss.bssserverapi.domain.research.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class GetResearchPagingResDto {

    private List<GetResearchResDto> getResearchResDtoList;
    private final boolean hasNext;

    @Builder
    public GetResearchPagingResDto(final boolean hasNext, final List<GetResearchResDto> getResearchResDtoList) {

        this.hasNext = hasNext;
        this.getResearchResDtoList = getResearchResDtoList;
    }
}
