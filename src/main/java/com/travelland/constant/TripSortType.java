package com.travelland.constant;

import lombok.Getter;

@Getter
public enum TripSortType {
    CREATED_AT("createdAt"),
    VIEW_COUNT("viewCount"),
    TITLE("title");

    private String value;

    TripSortType(String value) {
        this.value = value;
    }
}
