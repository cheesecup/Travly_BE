package com.travelland.repository.notification;

import com.travelland.constant.NotificationType;
import com.travelland.domain.Notification;
import com.travelland.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByNotificationTypeAndReceiverIdAndIsReadIsFalse(NotificationType type, Long receiverId);
    Optional<Notification> findByNotificationTypeAndReceiverAndTitle(NotificationType type, Member receiver, String title);
}
