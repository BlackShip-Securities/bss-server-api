package com.bss.bssserverapi.domain.research.dto;

import com.bss.bssserverapi.domain.research.Research;
import com.bss.bssserverapi.domain.tag.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class GetResearchPreviewResDto {

    private final Long id;
    private final String title;
    private final Long recommendCount;
    private final Long commentCount;
    private final Long targetPrice;
    private final LocalDate dateStart;
    private final LocalDate dateEnd;
    private final String userName;
    private final List<String> tagNameList;

    public static GetResearchPreviewResDto toDto(final Research research, final List<Tag> tagList) {

        return new GetResearchPreviewResDto(
                research.getId(),
                research.getTitle(),
                research.getRecommendCount(),
                research.getCommentCount(),
                research.getTargetPrice(),
                research.getDateStart(),
                research.getDateEnd(),
                research.getUser().getUserName(),
                tagList.stream().map(tag -> tag.getName()).toList());
    }
}