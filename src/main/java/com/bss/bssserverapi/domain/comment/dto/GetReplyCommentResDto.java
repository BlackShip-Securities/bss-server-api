package com.bss.bssserverapi.domain.comment.dto;

import com.bss.bssserverapi.domain.comment.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GetReplyCommentResDto {

    private Long id;
    private String content;
    private String userName;
    private Boolean isDeleted;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public GetReplyCommentResDto(final Long id, final String content, final String userName, final Boolean isDeleted, final LocalDateTime deletedAt, final LocalDateTime createdAt, final LocalDateTime updatedAt) {

        this.id = id;
        this.content = content;
        this.userName = userName;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    static public GetReplyCommentResDto toDto(final Comment comment) {

        return new GetReplyCommentResDto(
                comment.getId(),
                (comment.getIsDeleted().equals(Boolean.TRUE) ? "삭제된 댓글입니다. " : comment.getContent()),
                comment.getUser().getUserName(),
                comment.getIsDeleted(),
                comment.getDeletedAt(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
