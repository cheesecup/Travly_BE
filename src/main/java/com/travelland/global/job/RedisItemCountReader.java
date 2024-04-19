package com.travelland.global.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import java.util.*;
@Slf4j(topic = "Redis Item Reader : ")
public class RedisItemCountReader implements ItemReader<DataIntSet> {
    private final RedisTemplate<String, String> redisTemplate;
    private Iterator<Map.Entry<Long, Integer>> dataIterator;
    private final ScanOptions scanOptions;

    public RedisItemCountReader(RedisTemplate<String,String> redisTemplate, String pattern){
        this.redisTemplate = redisTemplate;
        this.scanOptions = ScanOptions.scanOptions().match(pattern).count(10).build();
        fetchDataFromRedis();
    }

    @Override
    public DataIntSet read() {
        if(dataIterator == null || !dataIterator.hasNext())
            return null;

        Map.Entry<Long, Integer> next = dataIterator.next();
        log.info("id : " + next.getKey() + "/////" + "value : " + next.getValue());
        return  new DataIntSet(next.getKey(),next.getValue());
    }

    private void fetchDataFromRedis() {
        RedisConnectionFactory redisConnectionFactory = redisTemplate.getConnectionFactory();

        if (redisConnectionFactory == null) {
            log.warn("Redis connection factory is null.");
            return;
        }

        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            Cursor<byte[]> cursor = connection.scan(scanOptions);
            List<Map.Entry<Long, Integer>> dataList = new ArrayList<>();

            while (cursor.hasNext()) {
                String key = new String(cursor.next());
                Set<String> value = redisTemplate.opsForSet().members(key);

                if (value == null)
                    break;

                Map.Entry<Long, Integer> dataEntry =
                            new AbstractMap.SimpleEntry<>(Long.parseLong(key.split(":")[2]), value.size());
                log.info(key + "value: " + value);
                dataList.add(dataEntry);
            }
            this.dataIterator = dataList.iterator();
        }
    }
}