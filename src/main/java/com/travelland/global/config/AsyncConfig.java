package com.travelland.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();	//스프링이 제공하는 스레드 풀 구현체
        taskExecutor.setCorePoolSize(1); //핵심 스레드 수 1로 설정 스레드 풀의 최소 스레드 수
        taskExecutor.setMaxPoolSize(5); //최대 스레드 수 5로 설정 풀이 생성하는 최대 스레드 수
        taskExecutor.setQueueCapacity(10); //작업 대기열의 크기 10
        taskExecutor.setThreadNamePrefix("async-thread-"); //각 스레드의 이름 설정
        taskExecutor.initialize();
        return taskExecutor;
    }
}