package com.travelland.global.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j(topic = "jobExecuteDuration : ")
public class JdbcJobExecutionListener implements JobExecutionListener {

    @Override
    public void afterJob(JobExecution jobExecution){
        Duration diff = Duration.between(jobExecution.getStartTime(),jobExecution.getEndTime());
        log.info(String.valueOf(diff.toMillis()));
    }

}
