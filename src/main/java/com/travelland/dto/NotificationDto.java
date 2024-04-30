package com.travelland.dto;

import com.travelland.constant.NotificationType;
import com.travelland.domain.Notification;
import lombok.Getter;

public class NotificationDto {

    @Getter
    public static class NotificationResponse {
        private String title;
        private String content;
        private String url;
        private Boolean isRead;
        private NotificationType notificationType;;

        public NotificationResponse(Notification notification) {
            this.title = notification.getTitle();
            this.content = notification.getContent();
            this.url = notification.getUrl();
            this.isRead = notification.getIsRead();
            this.notificationType = notification.getNotificationType();
        }
    }
}
