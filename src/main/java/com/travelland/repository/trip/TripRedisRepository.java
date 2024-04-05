package com.travelland.repository.trip;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TripRedisRepository {

    private final StringRedisTemplate redisTemplate;

    public void saveTripTotalElements() {

    }
}
