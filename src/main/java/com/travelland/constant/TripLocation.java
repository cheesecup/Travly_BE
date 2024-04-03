package com.travelland.constant;

import lombok.Getter;

@Getter
public enum TripLocation {
    BUSSAN("부산"),
    VIEW_COUNT("viewCount"),
    TITLE("title");

    private String value;

    TripLocation(String value) {
        this.value = value;
    }
}
