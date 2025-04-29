package com.bss.bssserverapi.domain.comment.dto.res;

import com.bss.bssserverapi.domain.comment.Comment;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GetReplyCommentResDto {

    private Long id;
    private String content;
    private String userName;
    private Boolean isDeleted;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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
