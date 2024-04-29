package com.travelland.service.notification;

import com.travelland.constant.NotificationType;
import com.travelland.domain.Notification;
import com.travelland.domain.member.Member;
import com.travelland.dto.NotificationDto;
import com.travelland.global.notification.Publisher;
import com.travelland.repository.notification.EmitterRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final long TIMEOUT = 60 * 60 * 1000L; // 1시간

    private final EmitterRepositoryImpl emitterRepository;

    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final Publisher redisPublisher;
    private final Subscriber redisSubscriber;

    public SseEmitter subscribe(Long memberId) {
        String emitterId = makeTimeIncludeId(memberId);
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(TIMEOUT));

        // 503 에러를 방지하기 위한 더미 이벤트 전송
        String eventId = makeTimeIncludeId(memberId);
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .name("sse")
                    .data("EventStream Created. [memberId=" + memberId + "]"));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
        }

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));
        emitter.onError((e) -> emitterRepository.deleteById(emitterId));

        ChannelTopic topic = new ChannelTopic(emitterId);
        redisMessageListenerContainer.addMessageListener(redisSubscriber, topic);

        return emitter;
    }

    private String makeTimeIncludeId(Long memberId) {
        return memberId + "_" + System.currentTimeMillis();
    }

    public void sendLostData(String lastEventId, Long memberId) {
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByMemberId(String.valueOf(memberId));
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(memberId));
        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> emitters.forEach(
                        (key, emitter) -> {
                            publish(key, new NotificationDto.NotificationResponse((Notification) entry.getValue()));
                        }));
    }

    // 알림 보내기(sender -> receiver)
    public void send(Member receiver, String title, String content, String url, NotificationType notificationType) {
        Notification notification = createNotification(receiver, title, content, url, notificationType);

        String receiverId = String.valueOf(receiver.getId());
        String eventId = receiverId + "_" + System.currentTimeMillis();
        emitterRepository.saveEventCache(eventId, notification);

        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByMemberId(receiverId);
        emitters.forEach(
                (key, emitter) -> {
                    publish(key, new NotificationDto.NotificationResponse(notification));
                }
        );
    }

    public void publish(String emitterId, NotificationDto.NotificationResponse response) {
        redisPublisher.publish(emitterId, response);
    }

    private Notification createNotification(Member receiver, String title, String content, String url, NotificationType notificationType) {
        return Notification.builder()
                .receiver(receiver)
                .title(title)
                .content(content)
                .url(url)
                .isRead(false)
                .notificationType(notificationType)
                .build();
    }
}
