package com.bss.bssserverapi.domain.comment.repository;

import com.bss.bssserverapi.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentJpaRepository extends JpaRepository<Comment, Long> {
}
