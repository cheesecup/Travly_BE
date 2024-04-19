package com.travelland.global.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import java.util.*;

@Slf4j(topic = "Redis Email Reader : ")
public class RedisItemEmailReader implements ItemReader<List<DataStrSet>> {
    private final RedisTemplate<String, String> redisTemplate;
    private Iterator<Map.Entry<Long, Set<String>>> dataIterator;
    private final ScanOptions scanOptions;

    public RedisItemEmailReader(RedisTemplate<String,String> redisTemplate, String pattern){
        this.redisTemplate = redisTemplate;
        this.scanOptions = ScanOptions.scanOptions().match(pattern).build();
        fetchDataFromRedis();
    }

    @Override
    public List<DataStrSet> read() {
        if(dataIterator == null || !dataIterator.hasNext())
            return null;

        Map.Entry<Long, Set<String>> next = dataIterator.next();
        List<DataStrSet> datas = new ArrayList<>();
        Long dataKey  = next.getKey();
        for(String str : next.getValue()){
            datas.add(new DataStrSet(dataKey,str));
        }
        return datas;
    }

    private void fetchDataFromRedis() {
        RedisConnectionFactory redisConnectionFactory = redisTemplate.getConnectionFactory();

        if (redisConnectionFactory == null) {
            log.warn("Redis connection factory is null.");
            return;
        }

        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            Cursor<byte[]> cursor = connection.scan(scanOptions);
            List<Map.Entry<Long, Set<String>>> dataList = new ArrayList<>();

            while (cursor.hasNext()) {
                String key = new String(cursor.next());
                Set<String> value = redisTemplate.opsForSet().members(key);

                if (value == null)
                    break;

                Map.Entry<Long, Set<String>> dataEntry =
                        new AbstractMap.SimpleEntry<>(Long.parseLong(key.split(":")[2]), value);

                dataList.add(dataEntry);
            }
            this.dataIterator = dataList.iterator();
        }
    }
}