package com.travelland.repository.notification;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Optional;

public interface EmitterRepository {
    SseEmitter save(String emitterId, SseEmitter sseEmitter);
    Optional<SseEmitter> findEmitterById(String emitterId);
    void deleteById(String id);
}