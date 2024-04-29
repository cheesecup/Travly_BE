package com.travelland.dto.trip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

public class TripImageDto {

    /**
     * 등록할 여행후기 이미지 정보를 담는 DTO
     */
    @Getter
    @ToString
    @AllArgsConstructor
    public static class CreateRequest {
        private String imageUrl;
        private String storeImageName;
    }
}
