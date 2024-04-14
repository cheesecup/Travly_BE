package com.travelland.global.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;

@Slf4j(topic = "Redis Item Reader : ")
public class RedisItemReader implements ItemReader<DataSet> {
    private final RedisTemplate<String, String> redisTemplate;
    private Iterator<Map.Entry<String, String>> dataIterator;

    public RedisItemReader(RedisTemplate<String,String> redisTemplate){
        this.redisTemplate = redisTemplate;
        fetchDataFromRedis();
    }

    @Override
    public DataSet read() {

        if(dataIterator == null)
            return null;

        if(!dataIterator.hasNext())
            return null;

        Map.Entry<String,String> next = dataIterator.next();
        return  new DataSet(Long.parseLong(next.getKey()),Integer.parseInt(next.getValue()));
    }

    private void fetchDataFromRedis() {
        Set<String> keys = redisTemplate.keys("*");
        List<Map.Entry<String, String>> dataList = new ArrayList<>();

        if (keys == null)
            return;

        for (String key : keys) {
            String value = redisTemplate.opsForValue().get(key);
            Map.Entry<String, String> entry = new AbstractMap.SimpleEntry<>(key, value);
            dataList.add(entry);
        }
        this.dataIterator = dataList.iterator();
    }
}