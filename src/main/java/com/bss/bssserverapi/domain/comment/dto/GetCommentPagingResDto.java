package com.bss.bssserverapi.domain.comment.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetCommentPagingResDto {

    private Long totalPage;
    private List<GetCommentResDto> getCommentResDtoList;

    @Builder
    public GetCommentPagingResDto(final Long totalPage, final List<GetCommentResDto> getCommentResDtoList) {

        this.totalPage = totalPage;
        this.getCommentResDtoList = getCommentResDtoList;
    }
}
