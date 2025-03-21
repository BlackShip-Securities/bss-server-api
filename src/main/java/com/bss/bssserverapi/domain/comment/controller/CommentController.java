package com.bss.bssserverapi.domain.comment.controller;

import com.bss.bssserverapi.domain.comment.dto.CreateCommentReqDto;
import com.bss.bssserverapi.domain.comment.dto.GetCommentResDto;
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

    @PostMapping("/researches/{researchId}/comments")
    public ResponseEntity<GetCommentResDto> createComment(
            @AuthenticationPrincipal final String userName,
            @PathVariable("researchId") final Long researchId,
            @RequestBody final CreateCommentReqDto createCommentReqDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(commentService.createComment(userName, researchId, createCommentReqDto));
    }
}
