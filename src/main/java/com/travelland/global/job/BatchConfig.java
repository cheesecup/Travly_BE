package com.travelland.global.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
//
//@Slf4j
//@Configuration
//@RequiredArgsConstructor
//public class BatchConfig {
//    private final JobRepository jobRepository;
//    private final PlatformTransactionManager platformTransactionManager;
//
//    @Bean
//    public Job dbSync() {
//        return new JobBuilder("DBSync", jobRepository)
//                .start(syncStep1())
//                .build();
//    }
//    @Bean
//    public Step syncStep1(){
//        return new StepBuilder("syncStep1", jobRepository)
//                .tasklet(syncTasklet(), platformTransactionManager).build();
//    }
//    @Bean
//    public Tasklet syncTasklet(){
//        return ((contribution, chunkContext) -> {
//            log.info("");
//            log.info(">>>>> This is task1");
//            log.info("");
//            return RepeatStatus.FINISHED;
//        });
//    }
//}