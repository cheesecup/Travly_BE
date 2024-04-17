package com.travelland.global.job;

import com.travelland.domain.trip.Trip;
import com.travelland.service.trip.TripSearchService;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.policy.CompositeCompletionPolicy;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.batch.repeat.policy.TimeoutTerminationPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JdbcBatchJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;
    private final TripSearchService tripSearchService;
    private final RedisTemplate<String,String> redisTemplate;
    private final EntityManagerFactory entityManagerFacory;

    private static final String JOB_NAME = "dbSync";
    private static final String STEP_NAME = "dbSyncEsStep";
    @Value("${spring.batch.chunk-size}")
    private int chunkSize;


    @Bean
    public Job jdbcJob(){
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(elasticSyncStep())
                .next(redisSyncStep())
                .build();
    }
    @JobScope
    @Bean
    public Step elasticSyncStep() {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<List<DataSet>,List<DataSet>>chunk(completionPolicy(), new DataSourceTransactionManager(dataSource))
                .reader(new ElasticsearchItemReader(tripSearchService, 10))
                .writer(chunkData -> chunkData.forEach(tripSearchService::syncViewCount))
                .transactionManager(platformTransactionManager)
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }

    @Bean
    public Step redisSyncStep() {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<DataSet,DataSet>chunk(completionPolicy(), new DataSourceTransactionManager(dataSource))
                .reader(redisItemReader())
                .writer(chunkData -> chunkData.forEach(null))
                .transactionManager(platformTransactionManager)
                .build();
    }

    @Bean
    public ItemReader<DataSet> redisItemReader(){
       return null;
    }

    @Bean
    public ItemWriter<Trip> jpaItemWriter(){
        return new JpaItemWriterBuilder<Trip>()
                .entityManagerFactory(entityManagerFacory)
                .build();
    }

    private CompletionPolicy completionPolicy() {
        CompositeCompletionPolicy policy =
                new CompositeCompletionPolicy();
        policy.setPolicies(
                new CompletionPolicy[] {
                        new TimeoutTerminationPolicy(1000),
                        new SimpleCompletionPolicy(chunkSize)});
        return policy;
    }
}
