package com.bss.bssserverapi.global.initializer;

import com.bss.bssserverapi.domain.stock.repository.StockJpaRepository;
import com.bss.bssserverapi.domain.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Order(4)
@Slf4j
public class CommentDummyInitRunner implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;
    private final UserJpaRepository userJpaRepository;
    private final StockJpaRepository stockJpaRepository;

    private static final int BATCH_SIZE = 5000;
    private static final int COMMENT_PER_RESEARCH = 1_000_000;

    @Override
    public void run(ApplicationArguments args) {

        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM comments", Long.class);
        if (count != null && count > 0) {
            log.info("💬 Comment dummy already exists.");
            return;
        }

        insertDummyComments();
        log.info("🎉 {}개 더미 Research 데이터 삽입 완료!", COMMENT_PER_RESEARCH);
    }

    private void insertDummyComments() {

        String insertCommentSql = """
            INSERT INTO comments (content, child_comment_count, recommend_count, is_deleted, deleted_at, user_id, research_id, parent_comment_id, created_at, updated_at)
            VALUES (?, 0, 0, FALSE, NULL, ?, ?, NULL, NOW(), NOW())
        """;

        List<Object[]> commentBatch = new ArrayList<>();

        for (int i = 0; i < COMMENT_PER_RESEARCH; i++) {
            commentBatch.add(new Object[]{
                    "댓글 내용_" + "bss_test_0" + "_" + "삼성전자" + "_" + i,
                    1,
                    1
            });

            if (commentBatch.size() % BATCH_SIZE == 0) {
                jdbcTemplate.batchUpdate(insertCommentSql, commentBatch);
                commentBatch.clear();
            }
        }
    }
}