package com.bss.bssserverapi.domain.comment.service;

import com.bss.bssserverapi.domain.comment.Comment;
import com.bss.bssserverapi.domain.comment.CommentRecommend;
import com.bss.bssserverapi.domain.comment.dto.*;
import com.bss.bssserverapi.domain.comment.repository.CommentJpaRepository;
import com.bss.bssserverapi.domain.comment.repository.CommentRecommendJpaRepository;
import com.bss.bssserverapi.domain.research.Research;
import com.bss.bssserverapi.domain.research.repository.ResearchJpaRepository;
import com.bss.bssserverapi.domain.user.User;
import com.bss.bssserverapi.domain.user.repository.UserJpaRepository;
import com.bss.bssserverapi.global.exception.ErrorCode;
import com.bss.bssserverapi.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentJpaRepository commentJpaRepository;
    private final ResearchJpaRepository researchJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final CommentRecommendJpaRepository commentRecommendJpaRepository;

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
    public GetCommentPagingResDto getCommentPagingByResearch(final Long researchId, final int page, final int limit){

        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "id"));

        Page<Long> commentIdPage = commentJpaRepository.findCommentIdPagingByResearchId(researchId, pageable);
        List<Long> commentIdList = commentIdPage.getContent();

        List<Comment> commentList = commentJpaRepository.findCommentsWithUserAndResearchByIdIn(commentIdList);

        return GetCommentPagingResDto.builder()
                .totalPage(Long.valueOf(commentIdPage.getTotalPages()))
                .getCommentResDtoList(commentList.stream()
                        .map(GetCommentResDto::toDto)
                        .toList())
                .build();
    }

    @Transactional(readOnly = true)
    public GetReplyCommentListResDto getReplyCommentListByParentComment(final Long parentCommentId){

        return GetReplyCommentListResDto.builder()
                .getReplyCommentResDtoList(commentJpaRepository.findCommentsByParentCommentId(parentCommentId).stream()
                        .map(GetReplyCommentResDto::toDto)
                        .toList())
                .build();
    }

    @Transactional
    public GetCommentResDto updateComment(final String userName, final Long commentId, final UpdateCommentReqDto updateCommentReqDto){

        User user = userJpaRepository.findByUserName(userName)
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND));
        Comment comment = commentJpaRepository.findById(commentId)
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, ErrorCode.COMMENT_NOT_FOUND));

        this.authorizeUserByComment(user, comment);

        comment.update(updateCommentReqDto.getContent());

        return GetCommentResDto.toDto(commentJpaRepository.save(comment));
    }

    private void authorizeUserByComment(final User user, final Comment comment) {

        String requestUser = user.getUserName();
        String commentUser = comment.getUser().getUserName();

        if(requestUser.equals(commentUser))
            return;

        throw new GlobalException(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED);
    }

    @Transactional
    public void updateCommentRecommend(final String userName, final Long commentId) {

        final User user = userJpaRepository.findByUserName(userName)
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND));
        final Comment comment = commentJpaRepository.findById(commentId)
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, ErrorCode.COMMENT_NOT_FOUND));

        CommentRecommend commentRecommend = this.getOrCreateCommentRecommend(user, comment);

        commentRecommend.updateRecommend();
    }

    private CommentRecommend getOrCreateCommentRecommend(final User user, final Comment comment ) {

        return commentRecommendJpaRepository.findByUserIdAndCommentId(user.getId(), comment.getId())
                .orElseGet(() -> commentRecommendJpaRepository.save(CommentRecommend.builder()
                        .recommend(Boolean.FALSE)
                        .user(user)
                        .comment(comment)
                        .build()));
    }

    @Transactional
    public void deleteComment(final String userName, final Long commentId){

        User user = userJpaRepository.findByUserName(userName)
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND));
        Comment comment = commentJpaRepository.findById(commentId)
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, ErrorCode.COMMENT_NOT_FOUND));

        this.authorizeUserByCommentAndResearch(user, comment);

        comment.softDelete();
    }

    private void authorizeUserByCommentAndResearch(final User user, final Comment comment) {

        String requestUser = user.getUserName();
        String commentUser = comment.getUser().getUserName();
        String researchUser = comment.getResearch().getUser().getUserName();

        if(requestUser.equals(commentUser) || requestUser.equals(researchUser))
            return;

        throw new GlobalException(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED);
    }
}
