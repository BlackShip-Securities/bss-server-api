package com.bss.bssserverapi.global.batch;

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
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class CryptoBatchConfig {

    private final JobLauncher jobLauncher;
    private final JobRepository jobRepository;
    private final CryptoJpaRepository cryptoJpaRepository;

    @Bean
    public Job cryptoJob(final JobRepository jobRepository, final Step cryptoStep){

        return new JobBuilder("cryptoJob", jobRepository)
                .start(cryptoStep)
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step cryptoStep(final JobRepository jobRepository, final PlatformTransactionManager transactionManager){

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
            throw new UnexpectedInputException("Binance API returned null response for /exchangeInfo");
        }
        List<Map<String, Object>> symbols = (List<Map<String, Object>>) response.get("symbols");

        return new ListItemReader<>(symbols);
    }

    @Bean
    public ItemProcessor<Map<String, Object>, Crypto> cryptoProcessor() {

        return item -> {
            String symbol = (String) item.get("symbol");
            if(cryptoJpaRepository.existsBySymbol(symbol)){
                return null;
            }

            return Crypto.builder()
                    .symbol(symbol)
                    .baseAsset((String) item.get("baseAsset"))
                    .quoteAsset((String) item.get("quoteAsset"))
                    .isSpotTradingAllowed(Boolean.TRUE.equals(item.get("isSpotTradingAllowed")))
                    .isMarginTradingAllowed(Boolean.TRUE.equals(item.get("isSpotTradingAllowed")))
                    .build();
        };
    }

    @Bean
    public ItemWriter<Crypto> cryptoWriter() {

        return items -> {
            List<Crypto> filtered = new ArrayList<>();
            for (Crypto c : items) {
                if (c != null) filtered.add(c);
            }
            cryptoJpaRepository.saveAll(filtered);
        };
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runOnceOnStartup() throws Exception {

        log.info("[Startup] Running crypto batch...");
        runCryptoJob();
    }

    @Scheduled(cron = "0 0 */6 * * *") // every 6 hours
    public void runPeriodically() throws Exception {

        log.info("[Scheduled] Running crypto batch...");
        runCryptoJob();
    }

    @Scheduled(cron = "0 0 */6 * * *") // per 6h
    public void runCryptoJob() throws Exception {

        JobParameters params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(cryptoJob(jobRepository, cryptoStep(jobRepository, null)), params);
    }
}
