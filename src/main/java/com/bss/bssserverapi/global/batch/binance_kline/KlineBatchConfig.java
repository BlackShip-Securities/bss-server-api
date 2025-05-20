package com.bss.bssserverapi.global.batch.binance_kline;

import com.bss.bssserverapi.domain.kline.Kline;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class KlineBatchConfig {

    private final JobRepository jobRepository;
    private final KlineReader reader;
    private final KlineProcessor processor;
    private final KlineWriter writer;

    @Bean
    public Job klineJob(@Qualifier("klineStep") final Step klineStep) {

        return new JobBuilder("klineJob", jobRepository)
                .start(klineStep)
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step klineStep(final PlatformTransactionManager transactionManager) {

        return new StepBuilder("klineStep", jobRepository)
                .<Kline, Kline>chunk(1000, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}


