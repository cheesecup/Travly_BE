package com.travelland.service.notification;

import com.travelland.constant.NotificationType;
import com.travelland.domain.Notification;
import com.travelland.domain.member.Member;
import com.travelland.dto.NotificationDto;
import com.travelland.global.notification.Publisher;
import com.travelland.repository.notification.EmitterRepositoryImpl;
import com.travelland.repository.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final long TIMEOUT = 60 * 60 * 1000L; // 1시간
    private final String CHANNEL_PREFIX = "CH";

    private final EmitterRepositoryImpl emitterRepository;
    private final NotificationRepository notificationRepository;

    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final Publisher redisPublisher;
    private final Subscriber redisSubscriber;

    public SseEmitter subscribe(Long memberId) {
        String emitterId = makeChannelName(memberId);
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(TIMEOUT));

        // 503 에러를 방지하기 위한 더미 이벤트 전송
        String eventId = memberId + "_" + System.currentTimeMillis();
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

    private String makeChannelName(Long memberId) {
        return CHANNEL_PREFIX + "_" + memberId;
    }

    public void sendPastData(Long memberId) {
        List<Notification> notifications = notificationRepository.findByNotificationTypeAndReceiverIdAndIsReadIsFalse(NotificationType.INVITE, memberId);
        notifications
                .forEach(notification ->
                        publish(makeChannelName(memberId), new NotificationDto.NotificationResponse(notification))
                );
    }

    // 알림 보내기(sender -> receiver)
    public void send(Member receiver, String title, String content, String url, NotificationType notificationType) {
        Notification notification = createNotification(receiver, title, content, url, notificationType);
        notificationRepository.save(notification);

        publish(makeChannelName(receiver.getId()), new NotificationDto.NotificationResponse(notification));
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
