package com.travelland.repository.notification;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@NoArgsConstructor
public class EmitterRepositoryImpl implements EmitterRepository {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
        emitters.put(emitterId, sseEmitter);
        return sseEmitter;
    }

    @Override
    public Optional<SseEmitter> findEmitterById(String emitterId) {
        return Optional.ofNullable(emitters.get(emitterId));
    }

    @Override
    public void deleteById(String id) {
        emitters.remove(id);
    }
}