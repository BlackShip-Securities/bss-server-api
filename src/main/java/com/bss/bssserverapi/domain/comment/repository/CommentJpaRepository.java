package com.bss.bssserverapi.domain.comment.repository;

import com.bss.bssserverapi.domain.comment.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentJpaRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findCommentsByResearchIdAndParentCommentIsNull(final Long researchId, final Pageable pageable);
}
