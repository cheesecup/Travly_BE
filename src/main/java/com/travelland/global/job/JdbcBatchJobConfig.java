package com.travelland.global.job;

import com.travelland.service.plan.PlanService;
import com.travelland.service.trip.TripService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
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
    private final TripService tripService;
    private final RedisTemplate<String, String> redisTemplate;
    private final PlanService planService;

    private static final String JOB_NAME = "dbSync";
    private static final String TRIP_VIEW_COUNT_STEP = "trip_view_count_step";
    private static final String TRIP_LIKE_STEP = "trip_like_step";
    private static final String TRIP_SCARP_STEP = "trip_scrap_step";
    private static final String PLAN_VIEW_COUNT_STEP = "plan_view_count_step";
    private static final String PLAN_LIKE_STEP = "plan_like_step";
    private static final String PLAN_SCARP_STEP = "plan_scrap_step";

    @Value("${spring.batch.chunk-size}")
    private int chunkSize;


//    @Bean
//    public Job jdbcJob(){
//        return new JobBuilder(JOB_NAME, jobRepository)
//                .incrementer(new RunIdIncrementer())
//                .start(redisTripViewCountSyncStep())
//                .next(redisTripLikeSyncStep())
//                .next(redisTripScrapSyncStep())
//                .next(redisPlanViewCountSyncStep())
//                .next(redisPlanLikeSyncStep())
//                .next(redisPlanScrapSyncStep())
//                .listener(new JdbcJobExecutionListener())
//                .build();
//    }


    @Bean
    public Job jdbcFlowJob() {

        Flow tripViewCountFlow = new FlowBuilder<Flow>("tripViewCountFlow")
                .start(redisTripViewCountSyncStep())
                .build();

        Flow tripLikeFlow = new FlowBuilder<Flow>("tripLikeFlow")
                .start(redisTripLikeSyncStep())
                .build();

        Flow tripScrapFlow = new FlowBuilder<Flow>("tripScrapFlow")
                .start(redisTripScrapSyncStep())
                .build();

        Flow planViewCountFlow = new FlowBuilder<Flow>("planViewCountFlow")
                .start(redisPlanViewCountSyncStep())
                .build();

        Flow planLikeFlow = new FlowBuilder<Flow>("planLikeFlow")
                .start(redisPlanLikeSyncStep())
                .build();

        Flow planScrapFlow = new FlowBuilder<Flow>("planScrapFlow")
                .start(redisPlanScrapSyncStep())
                .build();

        Flow parallelStepFlow = new FlowBuilder<Flow>("parallelStepFlow")
                .split(new SimpleAsyncTaskExecutor())
                .add(tripViewCountFlow, tripLikeFlow,tripScrapFlow,planViewCountFlow,planLikeFlow,planScrapFlow)
                .build();

        return new JobBuilder("jdbcFlowJob", jobRepository)
                .start(parallelStepFlow)
                .build().build();
    }


    @JobScope
    @Bean
    public Step redisTripViewCountSyncStep() {
        return new StepBuilder(TRIP_VIEW_COUNT_STEP, jobRepository)
                .<DataIntSet, DataIntSet>chunk(completionPolicy(), new DataSourceTransactionManager(dataSource))
                .reader(new RedisItemCountReader(redisTemplate,"tripViewCount*"))
                .writer(chunkData -> chunkData.forEach(tripService::updateViewCount))
                .transactionManager(platformTransactionManager)
                .build();
    }
    @JobScope
    @Bean
    public Step redisTripLikeSyncStep() {
        return new StepBuilder(TRIP_LIKE_STEP, jobRepository)
                .<List<DataStrSet>,List<DataStrSet>>chunk(completionPolicy(), new DataSourceTransactionManager(dataSource))
                .reader(new RedisItemEmailReader(redisTemplate,"tripLikes:tripId:*"))
                .writer(chunkData -> chunkData.forEach(tripService::syncTripLike))
                .transactionManager(platformTransactionManager)
                .build();
    }

    @JobScope
    @Bean
    public Step redisTripScrapSyncStep() {
        return new StepBuilder(TRIP_SCARP_STEP, jobRepository)
                .<List<DataStrSet>,List<DataStrSet>>chunk(completionPolicy(), new DataSourceTransactionManager(dataSource))
                .reader(new RedisItemEmailReader(redisTemplate,"tripScraps:tripId:*"))
                .writer(chunkData -> chunkData.forEach(tripService::syncTripScrap))
                .transactionManager(platformTransactionManager)
                .build();
    }

    @JobScope
    @Bean
    public Step redisPlanViewCountSyncStep() {
        return new StepBuilder(PLAN_VIEW_COUNT_STEP, jobRepository)
                .<DataIntSet, DataIntSet>chunk(completionPolicy(), new DataSourceTransactionManager(dataSource))
                .reader(new RedisItemCountReader(redisTemplate,"planViewCount*"))
                .writer(chunkData -> chunkData.forEach(planService::updateViewCount))
                .transactionManager(platformTransactionManager)
                .build();
    }

    @JobScope
    @Bean
    public Step redisPlanLikeSyncStep() {
        return new StepBuilder(PLAN_LIKE_STEP, jobRepository)
                .<List<DataStrSet>,List<DataStrSet>>chunk(completionPolicy(), new DataSourceTransactionManager(dataSource))
                .reader(new RedisItemEmailReader(redisTemplate,"planLikes:planId:*"))
                .writer(chunkData -> chunkData.forEach(planService::syncPlanLike))
                .transactionManager(platformTransactionManager)
                .build();
    }

    @JobScope
    @Bean
    public Step redisPlanScrapSyncStep() {
        return new StepBuilder(PLAN_SCARP_STEP, jobRepository)
                .<List<DataStrSet>,List<DataStrSet>>chunk(completionPolicy(), new DataSourceTransactionManager(dataSource))
                .reader(new RedisItemEmailReader(redisTemplate,"planScraps:planId:*"))
                .writer(chunkData -> chunkData.forEach(planService::syncPlanScrap))
                .transactionManager(platformTransactionManager)
                .build();
    }

    private CompletionPolicy completionPolicy() {
        CompositeCompletionPolicy policy =
                new CompositeCompletionPolicy();
        policy.setPolicies(
                new CompletionPolicy[] {
                        new TimeoutTerminationPolicy(3000),
                        new SimpleCompletionPolicy(chunkSize)});
        return policy;
    }
}
