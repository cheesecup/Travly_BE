package com.travelland.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.travelland.domain.Trip;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class TripDto {

    @Getter
    @AllArgsConstructor
    public static class CreateRequest {
        private String title;
        private String content;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate tripStartDate;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate tripEndDate;
        private Integer cost;
        private List<String> hashTag;
        private String area;
        private boolean isPublic;
    }

    @Getter
    @AllArgsConstructor
    public static class UpdateRequest {
        private String title;
        private String content;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate tripStartDate;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate tripEndDate;
        private Integer cost;
        private List<String> hashTag;
        private String area;
        private boolean isPublic;
    }

    @Getter
    @AllArgsConstructor
    public static class CreateResponse {
        private Long tripId;
    }

    @Getter
    @AllArgsConstructor
    public static class UpdateResponse {
        private Long tripId;
    }

    @Getter
    public static class GetResponse {
        //Trip 엔티티 값
        private Long tripId;
        private String title;
        private String content;
        private int cost;
        private String area;
        private LocalDate tripStartDate;
        private LocalDate tripEndDate;
        private int viewCount;
        private int likeCount;
        private String nickname;
        private LocalDate createdAt;

        private List<String> hashTag;
        private List<String> imageUrlList;

        private boolean isLike;
        private boolean isScrap;

        public GetResponse(Trip trip, List<String> hashTag, List<String> imageUrlList) {
            this.tripId = trip.getId();
            this.title = trip.getTitle();
            this.content = trip.getContent();
            this.cost = trip.getCost();
            this.area = trip.getArea();
            this.tripStartDate = trip.getTripStartDate();
            this.tripEndDate = trip.getTripEndDate();
            this.viewCount = trip.getViewCount();
            this.likeCount = trip.getLikeCount();
            this.nickname = trip.getMember().getNickname();
            this.createdAt = trip.getCreatedAt().toLocalDate();
            this.hashTag = hashTag;
            this.imageUrlList = imageUrlList;
            this.isLike = false;
            this.isScrap = false;
        }
    }

    @Getter
    public static class GetListResponse {
        private Long tripId;
        private String title;
        private String nickname;
        private String thumbnailUrl;
        private String tripPeriod;
        private int viewCount;
        private LocalDate createdAt;

        public GetListResponse(Trip trip, String thumbnailUrl) {
            this.tripId = trip.getId();
            this.title = trip.getTitle();
            this.nickname = trip.getMember().getNickname();
            this.thumbnailUrl = thumbnailUrl;
            this.tripPeriod = Period.between(trip.getTripStartDate(), trip.getTripEndDate()).getDays() + "일";
            this.viewCount = trip.getViewCount();
            this.createdAt = trip.getCreatedAt().toLocalDate();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class DeleteResponse {
        private boolean isDeleted;
    }
}
