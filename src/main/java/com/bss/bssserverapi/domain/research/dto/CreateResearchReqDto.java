package com.bss.bssserverapi.domain.research.dto;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class CreateResearchReqDto {

    @NotBlank(message = "제목은 필수 입력 값입니다.")
    @Size(max = 255, message = "제목은 최대 255자까지 입력할 수 있습니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력 값입니다.")
    private String content;

    @NotNull(message = "목표 금액은 필수 입력 값입니다.")
    @Positive(message = "목표 금액은 0보다 커야 합니다.")
    @Max(value = 9_223_372_036_854_775_807L, message = "목표 가격이 Long 범위를 초과할 수 없습니다.")
    private Long targetPrice;

    @NotNull(message = "시작 날짜는 필수 입력 값입니다.")
    @Future(message = "시작 날짜는 오늘 이후여야 합니다.")
    private LocalDate dateStart;

    @NotNull(message = "종료 날짜는 필수 입력 값입니다.")
    @Future(message = "종료 날짜는 오늘 이후여야 합니다.")
    private LocalDate dateEnd;

    @Builder
    public CreateResearchReqDto(String title, String content, Long targetPrice, LocalDate dateStart, LocalDate dateEnd) {

        this.title = title;
        this.content = content;
        this.targetPrice = targetPrice;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
    }
}