package com.bss.bssserverapi.domain.research.dto;

import com.bss.bssserverapi.domain.research.Research;
import com.bss.bssserverapi.domain.tag.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class GetResearchResDto {

    private final Long id;
    private final String title;
    private final String content;
    private final Long targetPrice;
    private final LocalDate dateStart;
    private final LocalDate dateEnd;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String userName;
    private final List<String> tagNameList;

    public static GetResearchResDto toDto(final Research research, final List<Tag> tagList) {

        return new GetResearchResDto(
                research.getId(),
                research.getTitle(),
                research.getContent(),
                research.getTargetPrice(),
                research.getDateStart(),
                research.getDateEnd(),
                research.getCreatedAt(),
                research.getUpdatedAt(),
                research.getUser().getUserName(),
                tagList.stream().map(tag -> tag.getName()).toList());
    }
}