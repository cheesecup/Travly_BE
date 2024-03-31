package com.travelland.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

public class TripImageDto {

    @Getter
    @ToString
    @AllArgsConstructor
    public static class CreateRequest {
        private String imageUrl;
        private String storeImageName;
    }
}
