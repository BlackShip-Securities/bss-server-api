package com.bss.bssserverapi.domain.comment.dto.res;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class GetCommentPagingResDto {

    private Long totalPage;
    private List<GetCommentResDto> getCommentResDtoList;

    @Builder
    public GetCommentPagingResDto(final Long totalPage, final List<GetCommentResDto> getCommentResDtoList) {

        this.totalPage = totalPage;
        this.getCommentResDtoList = getCommentResDtoList;
    }
}
