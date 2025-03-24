package com.bss.bssserverapi.domain.comment.repository;

import com.bss.bssserverapi.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentJpaRepository extends JpaRepository<Comment, Long> {

    List<Comment> findCommentsByResearchId(final Long researchId);
}
