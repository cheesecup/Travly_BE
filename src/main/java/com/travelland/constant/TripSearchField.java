package com.travelland.constant;

import lombok.Getter;

@Getter
public enum TripSearchField {

    HASHTAG("hashtag"), AREA("area");

    private final String field;

    TripSearchField(String field) {
        this.field = field;
    }
}
