package com.travelland.constant;

import lombok.Getter;

@Getter
public enum Gender {

    MALE("GENDER_MALE"), FEMALE("GENDER_FEMALE");

    private final String gender;

    Gender(String gender) {
        this.gender = gender;
    }
}
