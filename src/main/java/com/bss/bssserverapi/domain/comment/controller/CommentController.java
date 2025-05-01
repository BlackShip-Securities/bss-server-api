package com.bss.bssserverapi.domain.comment.controller;

import com.bss.bssserverapi.domain.comment.dto.req.CreateCommentReqDto;
import com.bss.bssserverapi.domain.comment.dto.req.UpdateCommentReqDto;
import com.bss.bssserverapi.domain.comment.dto.res.GetCommentPagingResDto;
import com.bss.bssserverapi.domain.comment.dto.res.GetCommentResDto;
import com.bss.bssserverapi.domain.comment.dto.res.GetReplyCommentListResDto;
import com.bss.bssserverapi.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 작성
     * */
    @PostMapping("/researches/{researchId}/comments")
    public ResponseEntity<GetCommentResDto> createComment(
            @AuthenticationPrincipal final String userName,
            @PathVariable("researchId") final Long researchId,
            @RequestBody final CreateCommentReqDto createCommentReqDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(commentService.createComment(userName, researchId, createCommentReqDto));
    }

    /**
     * 답글(대댓글) 작성
     * */
    @PostMapping("/comments/{commentId}/replyComments")
    public ResponseEntity<GetCommentResDto> createReplyComment(
            @AuthenticationPrincipal final String userName,
            @PathVariable("commentId") final Long commentId,
            @RequestBody final CreateCommentReqDto createCommentReqDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(commentService.createReplyComment(userName, commentId, createCommentReqDto));
    }

    @GetMapping("/researches/{researchId}/comments")
    public ResponseEntity<GetCommentPagingResDto> getCommentPagingByResearch(
            @PathVariable("researchId") final Long researchId,
            @RequestParam(value = "page", defaultValue = "0") final int page,
            @RequestParam(value = "limit", defaultValue = "10") final int limit) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(commentService.getCommentPagingByResearch(researchId, page, limit));
    }

    @GetMapping("/comments/{parentCommentId}/replyComments")
    public ResponseEntity<GetReplyCommentListResDto> getReplyCommentByComment(
            @PathVariable("parentCommentId") final Long parentCommentId) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(commentService.getReplyCommentListByParentComment(parentCommentId));
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<GetCommentResDto> updateComment(
            @AuthenticationPrincipal final String userName,
            @PathVariable("commentId") final Long commentId,
            @RequestBody final UpdateCommentReqDto updateCommentReqDto) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(commentService.updateComment(userName, commentId, updateCommentReqDto));
    }

    @PatchMapping("/comments/{commentId}/recommend")
    public ResponseEntity<?> updateCommentRecommend(
            @AuthenticationPrincipal final String userName,
            @PathVariable("commentId") final Long commentId) {

        commentService.updateCommentRecommend(userName, commentId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(
            @AuthenticationPrincipal final String userName,
            @PathVariable("commentId") final Long commentId){

        commentService.deleteComment(userName, commentId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
