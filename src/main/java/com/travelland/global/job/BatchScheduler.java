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

import static com.travelland.constant.Constants.TRIP_RECOMMEND_JOB_NAME;
/**
 * 좋아요 기반 추천 컨텐츠를 위한 batch 작업
 *
 */
@Slf4j(topic = "Scheduler Start : ")
@Component
@RequiredArgsConstructor
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;
    /**
     * 배치 작업을 해당 프로그램에서 실행할지 선택
     */
    @Value("${batchsync.isUpdate}")
    private boolean isUpdate;
    /**
     * 특정 시간마다 좋아요 기반 추천 컨텐츠를 위한 batch 작업 실행
     */
    @Scheduled(cron = "0 0 18 * * *") // 매일 3시마다 실행
    public void runJob() {
        if (!isUpdate) return;

        try {
            jobLauncher.run(jobRegistry.getJob(TRIP_RECOMMEND_JOB_NAME), new JobParametersBuilder()
                            .addString("time", LocalDateTime.now().toString())
                            .toJobParameters());
        } catch (NoSuchJobException | JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException |
                     JobParametersInvalidException | JobRestartException e) {
                throw new RuntimeException(e);
        }
    }
}
