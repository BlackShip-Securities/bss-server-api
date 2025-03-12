package com.bss.bssserverapi.domain.research.dto;

import com.bss.bssserverapi.domain.research.Research;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class GetResearchResDto {

    private String title;
    private String content;
    private Long targetPrice;
    private LocalDate dateStart;
    private LocalDate dateEnd;
    private String userName;

    @Builder
    public GetResearchResDto(String title, String content, Long targetPrice, LocalDate dateStart, LocalDate dateEnd, String userName) {

        this.title = title;
        this.content = content;
        this.targetPrice = targetPrice;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.userName = userName;
    }

    public static GetResearchResDto toDto(Research research) {

        return GetResearchResDto.builder()
                .title(research.getTitle())
                .content(research.getContent())
                .targetPrice(research.getTargetPrice())
                .dateStart(research.getDateStart())
                .dateEnd(research.getDateEnd())
                .userName(research.getUser().getUserName())
                .build();
    }
}
