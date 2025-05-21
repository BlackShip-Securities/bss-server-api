package com.bss.bssserverapi.global.batch.kline;

import com.bss.bssserverapi.global.batch.kline.event.KlineJobTriggerEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KlineJobLauncher {

    private final JobLauncher jobLauncher;
    private final Job klineJob;

    public KlineJobLauncher(final JobLauncher jobLauncher, @Qualifier("klineJob") final Job klineJob) {

        this.jobLauncher = jobLauncher;
        this.klineJob = klineJob;
    }

    @Async
    @EventListener
    public void handleKlineJobTrigger(final KlineJobTriggerEvent event) {

        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(klineJob, jobParameters);
            log.info("[KlineJob] Triggered by WebSocket connection");
        } catch (Exception e) {
            log.error("[KlineJob] Launch failed", e);
        }
    }
}