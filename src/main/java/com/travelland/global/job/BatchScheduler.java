package com.travelland.global.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j(topic = "Scheduler Start : ")
@Component
@RequiredArgsConstructor
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    @Value("${batchsync.isUpdate}")
    private boolean isUpdate;

    @Scheduled(cron = "0 0 3 * * *") // 매일 3시마다 실행
    public void runJob() {
        if (!isUpdate) return;

        log.info("start Job");
        try {
            jobLauncher.run(jobRegistry.getJob("dbSync"), new JobParametersBuilder()
                            .addString("time", LocalDateTime.now().toString())
                            .toJobParameters());
        } catch (NoSuchJobException | JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException |
                     JobParametersInvalidException | JobRestartException e) {
                throw new RuntimeException(e);
        }
    }
}
