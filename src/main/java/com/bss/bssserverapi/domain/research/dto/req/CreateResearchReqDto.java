package com.bss.bssserverapi.domain.research.dto.req;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateResearchReqDto {

    @NotBlank
    @Size(max = 255, message = "제목은 최대 255자까지 입력할 수 있습니다.")
    private String title;

    @NotBlank
    private String content;

    @NotNull
    @Positive(message = "목표 금액은 0보다 커야 합니다.")
    @Max(value = 9_223_372_036_854_775_807L, message = "목표 가격이 Long 범위를 초과할 수 없습니다.")
    private Long targetPrice;

    @NotNull
    @Future(message = "시작 날짜는 오늘 이후여야 합니다.")
    private LocalDate dateStart;

    @NotNull
    @Future(message = "종료 날짜는 오늘 이후여야 합니다.")
    private LocalDate dateEnd;

    @NotNull
    private Long stockId;

    @NotNull
    private List<String> tagNameList;

    @Builder
    public CreateResearchReqDto(final String title, final String content, final Long targetPrice, final LocalDate dateStart, final LocalDate dateEnd, final Long stockId, final List<String> tagNameList) {

        this.title = title;
        this.content = content;
        this.targetPrice = targetPrice;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.stockId = stockId;
        this.tagNameList = tagNameList;
    }
}