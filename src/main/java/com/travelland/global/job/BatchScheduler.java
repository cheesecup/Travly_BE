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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j(topic = "Schedular Start : ")
@Component
@RequiredArgsConstructor
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    //@Scheduled(cron = "0 0 4 * * *") //매일 04:00
    @Scheduled(cron = "0/10 * * * * *") // 10초마다 실행
    public void runJob() {
        log.info("start Job");
        try {
            jobLauncher.run(jobRegistry.getJob("jdbcFlowJob"),
                    new JobParametersBuilder()
                            .addString("time", LocalDateTime.now().toString())
                            .toJobParameters()
            );
        } catch (NoSuchJobException | JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException |
                 JobParametersInvalidException | JobRestartException e) {
            throw new RuntimeException(e);
        }
    }
}

