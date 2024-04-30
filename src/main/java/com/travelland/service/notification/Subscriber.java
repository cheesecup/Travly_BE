package com.travelland.service.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelland.domain.Notification;
import com.travelland.dto.NotificationDto;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.repository.notification.EmitterRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class Subscriber implements MessageListener {

    private final EmitterRepositoryImpl emitterRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String emitterId = new String(message.getChannel());
            Notification notification = objectMapper.readValue(message.getBody(), Notification.class);
            NotificationDto.NotificationResponse response = new NotificationDto.NotificationResponse(notification);

            String eventId = emitterId.split("_")[1] + "_" + System.currentTimeMillis();
            SseEmitter emitter = emitterRepository.findEmitterById(emitterId).orElseThrow(() -> new CustomException(ErrorCode.STATUS_NOT_LOGIN));
            try {
                emitter.send(SseEmitter.event()
                        .id(eventId)
                        .name("sse")
                        .data(response));
            } catch (IOException exception) {
                emitterRepository.deleteById(emitterId);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
