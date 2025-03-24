package com.bss.bssserverapi.domain.comment.dto;

import com.bss.bssserverapi.domain.comment.Comment;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class GetCommentResDto {

    private Long id;
    private String content;
    private String userName;
    private Long childCommentCount;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<GetCommentResDto> getReplyCommentResDtoList = new ArrayList<>();

    public GetCommentResDto(final Long id, final String content, final String userName, final Long childCommentCount, final Boolean isDeleted, final LocalDateTime createdAt, final LocalDateTime updatedAt, final List<GetCommentResDto> getReplyCommentResDtoList) {

        this.id = id;
        this.content = content;
        this.userName = userName;
        this.childCommentCount = childCommentCount;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.getReplyCommentResDtoList = getReplyCommentResDtoList;
    }

    static public GetCommentResDto toDto(final Comment comment) {

        List<GetCommentResDto> getCommentResDtoList = comment.getChildCommentList().stream()
                .map(GetCommentResDto::toDto)
                .toList();

        return new GetCommentResDto(
                comment.getId(),
                comment.getContent(),
                comment.getUser().getUserName(),
                comment.getChildCommentCount(),
                comment.getIsDeleted(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                getCommentResDtoList
        );
    }

    public void setGetReplyCommentResDtoList(final List<GetCommentResDto> getReplyCommentResDtoList){

        this.getReplyCommentResDtoList = getReplyCommentResDtoList;
    }
}
