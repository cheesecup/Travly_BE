package com.travelland.global.notification;

import com.travelland.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Publisher {

    private final RedisOperations<String, NotificationDto.NotificationResponse> eventRedisOperations;

    public void publish(String emitterId, NotificationDto.NotificationResponse response) {
        eventRedisOperations.convertAndSend(emitterId, response);
    }
}
