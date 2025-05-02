package com.bss.bssserverapi.comment;

import com.bss.bssserverapi.domain.comment.Comment;
import com.bss.bssserverapi.domain.comment.CommentRecommend;
import com.bss.bssserverapi.domain.comment.dto.req.CreateCommentReqDto;
import com.bss.bssserverapi.domain.comment.dto.req.UpdateCommentReqDto;
import com.bss.bssserverapi.domain.comment.dto.res.GetCommentPagingResDto;
import com.bss.bssserverapi.domain.comment.dto.res.GetCommentResDto;
import com.bss.bssserverapi.domain.comment.dto.res.GetReplyCommentListResDto;
import com.bss.bssserverapi.domain.comment.repository.CommentJpaRepository;
import com.bss.bssserverapi.domain.comment.repository.CommentRecommendJpaRepository;
import com.bss.bssserverapi.domain.comment.service.CommentService;
import com.bss.bssserverapi.domain.research.Research;
import com.bss.bssserverapi.domain.research.repository.ResearchJpaRepository;
import com.bss.bssserverapi.domain.user.User;
import com.bss.bssserverapi.domain.user.repository.UserJpaRepository;
import com.bss.bssserverapi.fixture.CommentFixture;
import com.bss.bssserverapi.fixture.UserFixture;
import com.bss.bssserverapi.global.exception.ErrorCode;
import com.bss.bssserverapi.global.exception.GlobalException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentJpaRepository commentJpaRepository;

    @Mock
    private CommentRecommendJpaRepository commentRecommendJpaRepository;

    @Mock
    private ResearchJpaRepository researchJpaRepository;

    @Mock
    private UserJpaRepository userJpaRepository;

    @InjectMocks
    private CommentService commentService;

    final String COMMENT_CONTENT = "comment_content";
    final String REPLY_COMMENT_CONTENT = "reply_comment_content";

    @Test
    @DisplayName("댓글 생성 성공")
    void createComment_success() {

        // given
        CreateCommentReqDto request = new CreateCommentReqDto(COMMENT_CONTENT);

        Comment comment = CommentFixture.댓글_생성_성공(COMMENT_CONTENT);
        User user = comment.getUser();
        Research research = comment.getResearch();

        given(userJpaRepository.findByUserName(user.getUserName())).willReturn(Optional.of(user));
        given(researchJpaRepository.findById(1L)).willReturn(Optional.of(research));
        given(commentJpaRepository.save(ArgumentMatchers.<Comment>any())).willAnswer(invocation -> invocation.getArgument(0));

        // when
        GetCommentResDto response = commentService.createComment(user.getUserName(), 1L, request);

        // then
        assertThat(response.getContent()).isEqualTo(COMMENT_CONTENT);
        then(researchJpaRepository).should().findById(1L);
        then(commentJpaRepository).should().save(ArgumentMatchers.<Comment>any());
    }

    @Test
    @DisplayName("답글 생성 성공")
    void createReplyComment_success() {

        // given
        CreateCommentReqDto request = new CreateCommentReqDto(REPLY_COMMENT_CONTENT);

        Comment parentComment = CommentFixture.댓글_생성_성공(COMMENT_CONTENT);
        User user = parentComment.getUser();

        given(userJpaRepository.findByUserName(user.getUserName())).willReturn(Optional.of(user));
        given(commentJpaRepository.findById(parentComment.getId())).willReturn(Optional.of(parentComment));
        given(commentJpaRepository.save(ArgumentMatchers.<Comment>any())).willAnswer(invocation -> invocation.getArgument(0));

        // when
        GetCommentResDto result = commentService.createReplyComment(user.getUserName(), parentComment.getId(), request);

        // then
        assertThat(result.getContent()).isEqualTo(REPLY_COMMENT_CONTENT);
        then(userJpaRepository).should().findByUserName(user.getUserName());
        then(commentJpaRepository).should().findById(parentComment.getId());
        then(commentJpaRepository).should().save(ArgumentMatchers.<Comment>any());
    }

    @Test
    @DisplayName("답글 생성 실패 - 부모 댓글이 답글인 경우")
    void createReplyComment_fail_dueToNestedReply() {

        // given
        CreateCommentReqDto request = new CreateCommentReqDto("reply to replyComment");

        Comment grandParentComment = CommentFixture.댓글_생성_성공(COMMENT_CONTENT);
        Comment parentComment = CommentFixture.답글_생성_성공(grandParentComment, REPLY_COMMENT_CONTENT);
        parentComment.setParentComment(grandParentComment);

        User user = parentComment.getUser();

        given(userJpaRepository.findByUserName(user.getUserName())).willReturn(Optional.of(user));
        given(commentJpaRepository.findById(parentComment.getId())).willReturn(Optional.of(parentComment));

        // when & then
        assertThatThrownBy(() -> commentService.createReplyComment(user.getUserName(), parentComment.getId(), request))
                .hasMessageContaining(ErrorCode.REPLY_TO_COMMENT_ONLY.getMessage());

        then(userJpaRepository).should().findByUserName(user.getUserName());
        then(commentJpaRepository).should().findById(parentComment.getId());
    }

    @Test
    @DisplayName("댓글 페이징 조회 성공")
    void getCommentPagingByResearch_success() {

        // given
        Long researchId = 1L;
        int page = 0;
        int limit = 10;

        List<Comment> comments = CommentFixture.댓글_페이징_조회(10l);
        List<Long> commentIds = comments.stream().map(Comment::getId).toList();

        Page<Long> commentIdPage = new PageImpl<>(commentIds, PageRequest.of(page, limit), commentIds.size());
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "id"));

        given(commentJpaRepository.findCommentIdPagingByResearchId(researchId, pageable)).willReturn(commentIdPage);
        given(commentJpaRepository.findCommentsWithUserByIdIn(commentIds)).willReturn(comments);

        // when
        GetCommentPagingResDto result = commentService.getCommentPagingByResearch(researchId, page, limit);

        // then
        assertThat(result.getTotalPage()).isEqualTo(1);
        assertThat(result.getGetCommentResDtoList()).hasSize(10);

        then(commentJpaRepository).should().findCommentIdPagingByResearchId(researchId, PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "id")));
        then(commentJpaRepository).should().findCommentsWithUserByIdIn(commentIds);
    }

    @Test
    @DisplayName("답글 리스트 조회 성공")
    void getReplyCommentListByParentComment_success() {

        // given
        Comment parentComment = CommentFixture.댓글_생성_성공(COMMENT_CONTENT);
        Comment replyComment1 = CommentFixture.답글_생성_성공(parentComment, REPLY_COMMENT_CONTENT);
        Comment replyComment2 = CommentFixture.답글_생성_성공(parentComment, REPLY_COMMENT_CONTENT);

        List<Comment> replyList = List.of(replyComment1, replyComment2);

        given(commentJpaRepository.findCommentsByParentCommentId(parentComment.getId())).willReturn(replyList);

        // when
        GetReplyCommentListResDto res = commentService.getReplyCommentListByParentComment(parentComment.getId());

        // then
        assertThat(res.getGetReplyCommentResDtoList()).hasSize(2);
        assertThat(res.getGetReplyCommentResDtoList().get(0).getContent()).isEqualTo(replyComment1.getContent());
        assertThat(res.getGetReplyCommentResDtoList().get(1).getContent()).isEqualTo(replyComment2.getContent());
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void updateComment_success() {

        // given
        Comment comment = CommentFixture.댓글_생성_성공(COMMENT_CONTENT);
        User user = comment.getUser();
        Long commentId = comment.getId();

        UpdateCommentReqDto request = new UpdateCommentReqDto("updated content");

        given(userJpaRepository.findByUserName(user.getUserName())).willReturn(Optional.of(user));
        given(commentJpaRepository.findById(commentId)).willReturn(Optional.of(comment));
        given(commentJpaRepository.save(ArgumentMatchers.<Comment>any())).willAnswer(invocation -> invocation.getArgument(0));

        // when
        GetCommentResDto result = commentService.updateComment(user.getUserName(), commentId, request);

        // then
        assertThat(result.getContent()).isEqualTo("updated content");
    }

    @Test
    @DisplayName("댓글 수정 실패 - 작성자 불일치")
    void updateComment_fail_unauthorizedUser() {

        // given
        Comment comment = CommentFixture.댓글_생성_성공(COMMENT_CONTENT);
        User anotherUser = UserFixture.사용자_1();
        Long commentId = comment.getId();

        UpdateCommentReqDto request = new UpdateCommentReqDto("malicious update");

        given(userJpaRepository.findByUserName(anotherUser.getUserName())).willReturn(Optional.of(anotherUser));
        given(commentJpaRepository.findById(commentId)).willReturn(Optional.of(comment));

        // when & then
        assertThatThrownBy(() -> commentService.updateComment(anotherUser.getUserName(), commentId, request))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining(ErrorCode.UNAUTHORIZED.getMessage());
    }

    @Test
    @DisplayName("댓글 추천 업데이트 성공 - 기존 추천 존재")
    void updateCommentRecommend_success_existingRecommend() {

        // given
        Comment comment = CommentFixture.댓글_생성_성공(COMMENT_CONTENT);
        User user = comment.getUser();
        CommentRecommend recommend = CommentRecommend.builder()
                .recommend(false)
                .user(user)
                .comment(comment)
                .build();

        given(userJpaRepository.findByUserName(user.getUserName())).willReturn(Optional.of(user));
        given(commentJpaRepository.findById(comment.getId())).willReturn(Optional.of(comment));
        given(commentRecommendJpaRepository.findByUserIdAndCommentId(user.getId(), comment.getId())).willReturn(Optional.of(recommend));

        // when
        commentService.updateCommentRecommend(user.getUserName(), comment.getId());

        // then
        assertThat(recommend.getRecommend()).isTrue();
        then(commentRecommendJpaRepository).should(never()).save(ArgumentMatchers.<CommentRecommend>any());
    }

    @Test
    @DisplayName("댓글 추천 업데이트 성공 - 기존 추천 없음, 새로 생성")
    void updateCommentRecommend_success_createNewRecommend() {

        // given
        Comment comment = CommentFixture.댓글_생성_성공(COMMENT_CONTENT);
        User user = comment.getUser();

        given(userJpaRepository.findByUserName(user.getUserName())).willReturn(Optional.of(user));
        given(commentJpaRepository.findById(comment.getId())).willReturn(Optional.of(comment));
        given(commentRecommendJpaRepository.findByUserIdAndCommentId(user.getId(), comment.getId())).willReturn(Optional.empty());
        given(commentRecommendJpaRepository.save(ArgumentMatchers.<CommentRecommend>any())).willAnswer(invocation -> invocation.getArgument(0));

        // when
        commentService.updateCommentRecommend(user.getUserName(), comment.getId());

        // then
        then(commentRecommendJpaRepository).should().save(ArgumentMatchers.<CommentRecommend>any());
    }

    @Test
    @DisplayName("댓글 삭제 성공 - 댓글 작성자")
    void deleteComment_success_byCommentAuthor() {

        // given
        Comment comment = CommentFixture.댓글_생성_성공(COMMENT_CONTENT);
        User commentUser = comment.getUser();

        given(userJpaRepository.findByUserName(commentUser.getUserName())).willReturn(Optional.of(commentUser));
        given(commentJpaRepository.findById(comment.getId())).willReturn(Optional.of(comment));

        // when
        commentService.deleteComment(commentUser.getUserName(), comment.getId());

        // then
        assertThat(comment.getIsDeleted()).isTrue();
    }

    @Test
    @DisplayName("댓글 삭제 성공 - 리서치 작성자")
    void deleteComment_success_byResearchAuthor() {

        // given
        Comment comment = CommentFixture.댓글_생성_성공(COMMENT_CONTENT);
        User researchAuthor = comment.getResearch().getUser();

        given(userJpaRepository.findByUserName(researchAuthor.getUserName())).willReturn(Optional.of(researchAuthor));
        given(commentJpaRepository.findById(comment.getId())).willReturn(Optional.of(comment));

        // when
        commentService.deleteComment(researchAuthor.getUserName(), comment.getId());

        // then
        assertThat(comment.getIsDeleted()).isTrue();
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 권한 없는 사용자")
    void deleteComment_fail_unauthorizedUser() {

        // given
        Comment comment = CommentFixture.댓글_생성_성공(COMMENT_CONTENT);
        User unauthorizedUser = UserFixture.사용자_2();

        given(userJpaRepository.findByUserName("intruder")).willReturn(Optional.of(unauthorizedUser));
        given(commentJpaRepository.findById(comment.getId())).willReturn(Optional.of(comment));

        // when & then
        assertThatThrownBy(() -> commentService.deleteComment("intruder", comment.getId()))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining(ErrorCode.UNAUTHORIZED.getMessage());
    }
}
