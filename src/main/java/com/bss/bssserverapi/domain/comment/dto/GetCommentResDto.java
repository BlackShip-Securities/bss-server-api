package com.bss.bssserverapi.domain.comment.dto;

import com.bss.bssserverapi.domain.comment.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GetCommentResDto {

    private Long id;
    private String content;
    private String userName;
    private Long childCommentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    static public GetCommentResDto toDto(final Comment comment) {

        return new GetCommentResDto(
                comment.getId(),
                comment.getContent(),
                comment.getUser().getUserName(),
                comment.getChildCommentCount(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
