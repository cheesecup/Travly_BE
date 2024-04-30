package com.travelland.global.job;

import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.repository.trip.TripLikeRepository;
import com.travelland.repository.trip.TripRecommendRepository;
import com.travelland.repository.trip.TripRepository;
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
/**
 * 좋아요 기반 추천 컨텐츠를 위한 batch 작업
 *
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class TripRecommendJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;
    private final TripLikeRepository tripLikeRepository;
    private final TripRepository tripRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final TripRecommendRepository tripRecommendRepository;
    private static final String TRIP_LIKE_RECOMMEND_STEP = "trip_like_recommend_step";
    private static final String TRIP_RECOMMEND = "trip_recommend:";
    private static final int WORKER_SIZE = 2;
    private static final int RECOMMEND_SIZE = 5;

    @Value("${spring.batch.chunk-size}")
    private int chunkSize;
    /**
     * DB 에서 tripId를 꺼내온 후 해당하는 id의 추천 list를 분석해 Redis에 저장
     */
    @Bean
    public Job jdbcJob() {
        return new JobBuilder(TRIP_RECOMMEND_JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(tripLikeRecommendStep())
                .build();
    }
    /**
     * DB 에서 tripId를 꺼내온 후 해당하는 id의 추천 list를 분석해 Redis에 저장
     */
    @JobScope
    @Bean
    public Step tripLikeRecommendStep(){
        return new StepBuilder(TRIP_LIKE_RECOMMEND_STEP, jobRepository)
                .<Long, DataSet>chunk(completionPolicy(), new DataSourceTransactionManager(dataSource))
                .reader(jdbcPagingItemReader())
                .processor(itemProcessor())
                .writer(this::writeChunkDataToRedis)
                .transactionManager(platformTransactionManager)
                .taskExecutor(taskExecutor())
                .listener(jobExecutionListener(taskExecutor()))
                .build();
    }
    /**
     * DB 에서 tripId를 Paging 형식으로 가져오기
     * Paging: multi - thread 에 대해 안전
     */
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
    /**
     * DB 에서 사용할 SQL 문
     */
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
    /**
     * DB 에서 가져온 id 에 대한 추천 여행 정보 list 분석
     */
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

            if(res.size() > RECOMMEND_SIZE) {
                List<Long> subRes =  res.subList(0, RECOMMEND_SIZE+1);
                subRes.remove(id);
                return new DataSet(id, subRes.stream().limit(RECOMMEND_SIZE).map(Object::toString).toList());
            }

            res.remove(id);

            if(res.size() == RECOMMEND_SIZE){
                return new DataSet(id, res.stream().map(Object::toString).toList());
            }

            String content =
            tripRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND)).getContent();

            tripRecommendRepository.recommendByContent(content,RECOMMEND_SIZE - res.size())
                    .getSearchHits().forEach(data -> res.add(data.getContent().getId()));

            return new DataSet(id, res.stream().map(Object::toString).toList());
        };
    }

    /**
     * 여행 정보 id 에 대한 추천 여행 정보 list Redis에 저장
     */
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
    /**
     * chunk 단위 멀티스레드 설정
     */
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
    /**
     *  작업 종료시 ThreadPool 닫기
     */
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
    /**
     *  작업이 완료되었는지 여부를 판단하는 데 사용되는 규칙
     */
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