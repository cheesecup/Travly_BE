package com.travelland.constant;

import lombok.Getter;

@Getter
public enum NotificationType {
    INVITE("INVITE"),
    AGREE("AGREE"),
    DISAGREE("DISAGREE"),

    VOTE("VOTE");

    private final String notify;

    NotificationType(String notify) {
        this.notify = notify;
    }
}
