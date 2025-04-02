package com.bss.bssserverapi.domain.comment.dto;

import com.bss.bssserverapi.domain.comment.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GetCommentResDto {

    private Long id;
    private String content;
    private String userName;
    private Long childCommentCount;
    private Long recommendCount;
    private Boolean isDeleted;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public GetCommentResDto(final Long id, final String content, final String userName, final Long childCommentCount, final Long recommendCount, final Boolean isDeleted, final LocalDateTime deletedAt, final LocalDateTime createdAt, final LocalDateTime updatedAt) {

        this.id = id;
        this.content = content;
        this.userName = userName;
        this.childCommentCount = childCommentCount;
        this.recommendCount = recommendCount;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    static public GetCommentResDto toDto(final Comment comment) {

        return new GetCommentResDto(
                comment.getId(),
                (comment.getIsDeleted().equals(Boolean.TRUE) ? "삭제된 댓글입니다. " : comment.getContent()),
                comment.getUser().getUserName(),
                comment.getChildCommentCount(),
                comment.getRecommendCount(),
                comment.getIsDeleted(),
                comment.getDeletedAt(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
