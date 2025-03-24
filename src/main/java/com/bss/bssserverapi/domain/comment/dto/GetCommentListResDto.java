package com.bss.bssserverapi.domain.comment.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetCommentListResDto {

    private List<GetCommentResDto> getCommentResDtoList;

    @Builder
    public GetCommentListResDto(final List<GetCommentResDto> getCommentResDtoList) {

        this.getCommentResDtoList = getCommentResDtoList;
    }
}
