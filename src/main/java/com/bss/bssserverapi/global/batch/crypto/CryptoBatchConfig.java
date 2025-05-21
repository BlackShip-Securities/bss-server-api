package com.bss.bssserverapi.global.batch.crypto;

import com.bss.bssserverapi.domain.crypto.Crypto;
import com.bss.bssserverapi.domain.crypto.repository.CryptoJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class CryptoBatchConfig {

    private final JobLauncher jobLauncher;
    private final JobRepository jobRepository;
    private final CryptoJpaRepository cryptoJpaRepository;

    private Set<String> existingSymbols = new HashSet<>();

    @Bean
    public Job cryptoJob(@Qualifier("cryptoStep") final Step cryptoStep) {

        return new JobBuilder("cryptoJob", jobRepository)
                .start(cryptoStep)
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step cryptoStep(final PlatformTransactionManager transactionManager) {

        return new StepBuilder("cryptoStep", jobRepository)
                .<Map<String, Object>, Crypto>chunk(100, transactionManager)
                .reader(cryptoReader())
                .processor(cryptoProcessor())
                .writer(cryptoWriter())
                .build();
    }

    @Bean
    @SuppressWarnings("unchecked")
    public ItemReader<Map<String, Object>> cryptoReader() {

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.binance.com/api/v3/exchangeInfo";
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null) {
            throw new UnexpectedInputException("Binance API returned null");
        }
        return new ListItemReader<>((List<Map<String, Object>>) response.get("symbols"));
    }

    @Bean
    public ItemProcessor<Map<String, Object>, Crypto> cryptoProcessor() {

        return item -> {
            String symbol = (String) item.get("symbol");

            if (existingSymbols.contains(symbol))   return null;

            return Crypto.builder()
                    .symbol(symbol)
                    .baseAsset((String) item.get("baseAsset"))
                    .quoteAsset((String) item.get("quoteAsset"))
                    .isSpotTradingAllowed(Boolean.TRUE.equals(item.get("isSpotTradingAllowed")))
                    .isMarginTradingAllowed(Boolean.TRUE.equals(item.get("isMarginTradingAllowed")))
                    .build();
        };
    }

    @Bean
    public ItemWriter<Crypto> cryptoWriter() {

        return items -> {
            for (Crypto crypto : items) {
                if (crypto != null) {
                    cryptoJpaRepository.save(crypto); // 예외 발생 X
                }
            }
        };
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadExistingSymbols() throws Exception{

        runCryptoJob();
    }

    @Scheduled(cron = "0 0 */6 * * *") // 매 6시간마다 실행
    public void runScheduledJob() throws Exception {

        runCryptoJob();
    }

    public void runCryptoJob() throws Exception {

        log.info("[Startup] Preloading existing symbols...");
        existingSymbols = new HashSet<>(cryptoJpaRepository.findAllSymbols());
        log.info("[Startup] Loaded {} symbols.", existingSymbols.size());

        JobParameters params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(cryptoJob(cryptoStep(null)), params);
    }
}