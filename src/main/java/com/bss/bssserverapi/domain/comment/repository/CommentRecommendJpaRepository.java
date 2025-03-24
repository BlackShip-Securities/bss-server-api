package com.bss.bssserverapi.domain.comment.repository;

import com.bss.bssserverapi.domain.comment.CommentRecommend;
import com.bss.bssserverapi.domain.research.ResearchRecommend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRecommendJpaRepository extends JpaRepository<CommentRecommend, Long> {

    Optional<CommentRecommend> findByUserIdAndCommentId(final Long userId, final Long commentId);
}
