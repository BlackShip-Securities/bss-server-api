package com.bss.bssserverapi.domain.comment.service;

import com.bss.bssserverapi.domain.comment.Comment;
import com.bss.bssserverapi.domain.comment.dto.CreateCommentReqDto;
import com.bss.bssserverapi.domain.comment.dto.GetCommentListResDto;
import com.bss.bssserverapi.domain.comment.dto.GetCommentResDto;
import com.bss.bssserverapi.domain.comment.repository.CommentJpaRepository;
import com.bss.bssserverapi.domain.research.Research;
import com.bss.bssserverapi.domain.research.repository.ResearchJpaRepository;
import com.bss.bssserverapi.domain.user.User;
import com.bss.bssserverapi.domain.user.repository.UserJpaRepository;
import com.bss.bssserverapi.global.exception.ErrorCode;
import com.bss.bssserverapi.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentJpaRepository commentJpaRepository;
    private final ResearchJpaRepository researchJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Transactional
    public GetCommentResDto createComment(final String userName, final Long researchId, final CreateCommentReqDto createCommentReqDto) {

        User user = userJpaRepository.findByUserName(userName)
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND));
        Research research = researchJpaRepository.findById(researchId)
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, ErrorCode.RESEARCH_NOT_FOUND));

        Comment comment = Comment.builder()
                .content(createCommentReqDto.getContent())
                .user(user)
                .research(research)
                .build();

        research.addComment();

        return GetCommentResDto.toDto(commentJpaRepository.save(comment));
    }

    @Transactional
    public GetCommentResDto createReplyComment(final String userName, final Long researchId, final Long commentId, final CreateCommentReqDto createCommentReqDto) {

        User user = userJpaRepository.findByUserName(userName)
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND));
        Research research = researchJpaRepository.findById(researchId)
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, ErrorCode.RESEARCH_NOT_FOUND));
        Comment parentComment = commentJpaRepository.findById(commentId)
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, ErrorCode.COMMENT_NOT_FOUND));

        Comment comment = Comment.builder()
                .content(createCommentReqDto.getContent())
                .user(user)
                .research(research)
                .build();

        comment.setParentComment(parentComment);
        research.addComment();

        return GetCommentResDto.toDto(commentJpaRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public GetCommentListResDto getCommentListByResearch(final Long researchId){

        return GetCommentListResDto.builder()
                .getCommentResDtoList(commentJpaRepository.findCommentsByResearchId(researchId)
                        .stream().map(GetCommentResDto::toDto)
                        .toList())
                .build();
    }
}
