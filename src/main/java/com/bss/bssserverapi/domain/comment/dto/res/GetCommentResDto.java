package com.bss.bssserverapi.domain.comment.dto.res;

import com.bss.bssserverapi.domain.comment.Comment;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
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
