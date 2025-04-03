package com.bss.bssserverapi.domain.comment.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GetReplyCommentListResDto {

    private List<GetReplyCommentResDto> getReplyCommentResDtoList = new ArrayList<>();

    @Builder
    public GetReplyCommentListResDto(final List<GetReplyCommentResDto> getReplyCommentResDtoList) {

        this.getReplyCommentResDtoList = getReplyCommentResDtoList;
    }
}
