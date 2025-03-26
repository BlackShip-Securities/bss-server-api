package com.bss.bssserverapi.global.initializer;

import com.bss.bssserverapi.domain.stock.Stock;
import com.bss.bssserverapi.domain.stock.repository.StockJpaRepository;
import com.bss.bssserverapi.domain.user.User;
import com.bss.bssserverapi.domain.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
@Order(2)
public class ResearchDummyInitRunner implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;
    private final UserJpaRepository userJpaRepository;
    private final StockJpaRepository stockJpaRepository;

    private static final int BATCH_SIZE = 5000;
    private static final int RESEARCH_PER_STOCK = 100000;
    private static final int TAGS_PER_RESEARCH = 5;

    private long tagSequence = 1L;
    private long researchSequence = 1L; // 직접 태그 ID 시퀀스를 관리


    @Override
    public void run(ApplicationArguments args) {

        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM research", Long.class);
        if (count != null && count > 0) {
            log.info("🔍 Research dummy already exists.");
            return;
        }

        insertDummyData();

        log.info("🎉 {}개 더미 Research 데이터 삽입 완료!", researchSequence - 1);
    }

    private void insertDummyData() {

        List<User> users = userJpaRepository.findAll();
        List<Stock> stocks = stockJpaRepository.findAll();

        String insertResearchSql = """
            INSERT INTO research (title, content, target_price, date_start, date_end, user_id, stock_id, recommend_count, comment_count, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, 0, 0, NOW(), NOW())
        """;
        String insertTagSql = """
            INSERT INTO tag (id, name, created_at)
            VALUES (?, ?, NOW())
        """;
        String insertResearchTagSql = """
            INSERT INTO research_tag (research_id, tag_id) 
            VALUES (?, ?)
        """;

        // 100
        for (User user : users) {
            List<Object[]> researchBatch = new ArrayList<>();
            List<Object[]> tagBatch = new ArrayList<>();
            List<Object[]> researchTagBatch = new ArrayList<>();

            // 200 = 2 * 100
            for (int k = 0; k < 2; k ++) {
                Stock stock = stocks.get(k);

                // 20,000,000 = 100,000 * 2 * 100
                for (int i = 0; i < RESEARCH_PER_STOCK; i++) {
                    String title = "RESEARCH_" + user.getUserName() + "_" + stock.getName() + "_" + i;
                    Long researchId = researchSequence++;

                    // Research insert row
                    researchBatch.add(new Object[]{
                            title,
                            "This is the content for research.",
                            10000L,
                            Date.valueOf(LocalDate.now().plusDays(1)),
                            Date.valueOf(LocalDate.now().plusDays(10)),
                            user.getId(),
                            stock.getId()
                    });

                    // Tags per research
                    for (int j = 0; j < TAGS_PER_RESEARCH; j++) {
                        String tagName = "TAG_" + user.getUserName() + "_" + stock.getName() + "_" + i + "_" + j;
                        Long tagId = tagSequence++;
                        tagBatch.add(new Object[]{tagId, tagName});

                        // research_tag row
                        researchTagBatch.add(new Object[]{researchId, tagId});
                    }

                    if (researchBatch.size() % BATCH_SIZE == 0) {
                        jdbcTemplate.batchUpdate(insertResearchSql, researchBatch);
                        jdbcTemplate.batchUpdate(insertTagSql, tagBatch);
                        jdbcTemplate.batchUpdate(insertResearchTagSql, researchTagBatch);

                        log.info("🔨 Research Id: {}, Batch: {}, User: {}", researchId, researchBatch.size(), user.getUserName());

                        researchBatch.clear();
                        tagBatch.clear();
                        researchTagBatch.clear();
                    }
                }
            }
        }
    }
}