package com.travelland.global.job;

import com.travelland.repository.trip.TripLikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.policy.CompositeCompletionPolicy;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.batch.repeat.policy.TimeoutTerminationPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.travelland.constant.Constants.TRIP_RECOMMEND_JOB_NAME;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TripRecommendJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;
    private final TripLikeRepository tripLikeRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String TRIP_RECOMMEND_STEP = "trip_recommend_step";
    private static final String TRIP_RECOMMEND = "trip_recommend:";
    private static final int WORKER_SIZE = 2;

    @Value("${spring.batch.chunk-size}")
    private int chunkSize;

    @Bean
    public Job jdbcJob() {
        return new JobBuilder(TRIP_RECOMMEND_JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(jpaStep())
                .build();
    }

    @JobScope
    @Bean
    public Step jpaStep(){
        return new StepBuilder(TRIP_RECOMMEND_STEP, jobRepository)
                .<Long, DataSet>chunk(completionPolicy(), new DataSourceTransactionManager(dataSource))
                .reader(jdbcPagingItemReader())
                .processor(itemProcessor())
                .writer(this::writeChunkDataToRedis)
//                .writer(chunkData -> {
//                    for(DataSet data : chunkData){
//                        if(data.getRecommendIds().isEmpty())
//                            continue;
//                        redisTemplate.delete(TRIP_RECOMMEND + data.getId());
//                        redisTemplate.opsForList().rightPushAll(TRIP_RECOMMEND + data.getId(), data.getRecommendIds());
//                    }
//                })
                .transactionManager(platformTransactionManager)
                .taskExecutor(taskExecutor())
                .listener(jobExecutionListener(taskExecutor()))
                .build();
    }

    @Bean
    public JdbcPagingItemReader<Long> jdbcPagingItemReader() {
        return new JdbcPagingItemReaderBuilder<Long>()
                .name("jdbcPageItemReader")
                .dataSource(dataSource)
                .pageSize(chunkSize)
                .queryProvider(createQueryProvider())
                .rowMapper(((rs, rowNum) -> rs.getLong(1)))
                .build();
    }

    private PagingQueryProvider createQueryProvider() {
        SqlPagingQueryProviderFactoryBean providerFactoryBean = new SqlPagingQueryProviderFactoryBean();
        providerFactoryBean.setDataSource(dataSource);
        providerFactoryBean.setSelectClause("select t.id");
        providerFactoryBean.setFromClause("from trip t");
        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("id", Order.ASCENDING);
        providerFactoryBean.setSortKeys(sortKeys);
        try {
            return providerFactoryBean.getObject();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Bean
    public ItemProcessor<Long, DataSet> itemProcessor(){
        return id ->{
            Map<Long, Integer> recommendCount = new HashMap<>();

            for(Long memberId : tripLikeRepository.getMemberIdsByTripId(id,10000,1)){
                tripLikeRepository.getTripIdsByMemberId(memberId, 10000, 1)
                        .forEach(tripId ->
                                recommendCount.put(tripId, recommendCount.getOrDefault(tripId, 0) + 1));
            }

            List<Long> res = new ArrayList<>(recommendCount.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .toList());

            if(res.size() < 6) {
                res.remove(id);
                return new DataSet(id, res.stream().map(Object::toString).toList());
            }

            List<Long> subRes =  res.subList(0,6);
            subRes.remove(id);
            return new DataSet(id, subRes.stream().limit(5).map(Object::toString).toList());
        };
    }

    private void writeChunkDataToRedis(Chunk<? extends DataSet> chunkData) {
        redisTemplate.executePipelined((RedisCallback<Object>) redisConnection -> {
            chunkData.forEach(data -> {
                if (data.getRecommendIds().isEmpty()) return;
                byte[] serializedKey = redisTemplate.getStringSerializer().serialize(TRIP_RECOMMEND + data.getId());
                if(serializedKey == null) return;
                redisConnection.keyCommands().del(serializedKey);

                data.getRecommendIds().stream()
                        .map(id -> redisTemplate.getStringSerializer().serialize(id))
                        .forEach(serializedId -> redisConnection.listCommands().rPush(serializedKey, serializedId));
            });
            return null;
        });
    }

@Bean
public TaskExecutor taskExecutor(){
    ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
    threadPoolTaskExecutor.setCorePoolSize(WORKER_SIZE);
    threadPoolTaskExecutor.setMaxPoolSize(WORKER_SIZE);
    threadPoolTaskExecutor.setThreadNamePrefix("exe-");
    threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
    threadPoolTaskExecutor.setAllowCoreThreadTimeOut(true);
    threadPoolTaskExecutor.setKeepAliveSeconds(1);
    threadPoolTaskExecutor.initialize();
    return threadPoolTaskExecutor;
}

public JobExecutionListener jobExecutionListener(TaskExecutor taskExecutor){
        return new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
                log.info("Job Start");
            }
            @Override
            public void afterJob(JobExecution jobExecution) {
                ((ThreadPoolTaskExecutor) taskExecutor).shutdown();
            }
        };
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