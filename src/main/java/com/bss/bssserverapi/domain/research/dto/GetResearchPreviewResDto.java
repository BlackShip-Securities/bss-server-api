package com.bss.bssserverapi.domain.research.dto;

import com.bss.bssserverapi.domain.research.Research;
import com.bss.bssserverapi.domain.tag.Tag;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class GetResearchPreviewResDto {

    private final String title;
    private final Long targetPrice;
    private final LocalDate dateStart;
    private final LocalDate dateEnd;
    private final String userName;
    private final List<String> tagNameList;

    public GetResearchPreviewResDto(final String title, final Long targetPrice, final LocalDate dateStart, final LocalDate dateEnd, final String userName, final List<String> tagNameList) {

        this.title = title;
        this.targetPrice = targetPrice;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.userName = userName;
        this.tagNameList = tagNameList;
    }

    public static GetResearchPreviewResDto toDto(final Research research, final List<Tag> tagList) {

        return new GetResearchPreviewResDto(
                research.getTitle(),
                research.getTargetPrice(),
                research.getDateStart(),
                research.getDateEnd(),
                research.getUser().getUserName(),
                tagList.stream().map(tag -> tag.getName()).toList());
    }
}