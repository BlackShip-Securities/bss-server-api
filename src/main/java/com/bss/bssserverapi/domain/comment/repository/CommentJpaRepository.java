package com.bss.bssserverapi.domain.comment.repository;

import com.bss.bssserverapi.domain.comment.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentJpaRepository extends JpaRepository<Comment, Long> {

    @Query(value = """
        SELECT c.id FROM Comment c
        WHERE c.research.id = :researchId
        AND c.parentComment IS NULL
        """,
            countQuery = """
        SELECT COUNT(c) FROM Comment c
        WHERE c.research.id = :researchId
        AND c.parentComment IS NULL
        """
    )
    Page<Long> findCommentIdPagingByResearchId(@Param("researchId") final Long researchId, final Pageable pageable);

    @Query("""
        SELECT c FROM Comment c
        JOIN FETCH c.user
        WHERE c.id IN :commentIds
        ORDER BY c.id DESC
    """)
    List<Comment> findCommentsWithUserByIdIn(@Param("commentIds") final List<Long> commentIds);

    @Query("""
        SELECT c FROM Comment c 
        JOIN FETCH c.user 
        WHERE c.parentComment.id = :parentCommentId 
        ORDER BY c.id DESC
    """)
    List<Comment> findCommentsByParentCommentId(@Param("parentCommentId") final Long parentCommentId);
}
