package com.travelland.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.travelland.domain.trip.Trip;
import com.travelland.domain.trip.TripLike;
import com.travelland.domain.trip.TripScrap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class TripDto {

    @Getter
    @AllArgsConstructor
    public static class Create {
        private String title;
        private String content;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate tripStartDate;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate tripEndDate;

        private Integer cost;
        private List<String> hashTag;
        private String address;
        private String placeName;
        private String x;
        private String y;
        private boolean isPublic;
    }

    @Getter
    @AllArgsConstructor
    public static class Update {
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
    public static class Id {
        private Long tripId;
    }

    @Getter
    @AllArgsConstructor
    public static class Get {
        //Trip 엔티티 값
        private Long tripId;
        private String title;
        private String content;
        private int cost;
        private String area;
        private String address;
        private String placeName;
        private String x;
        private String y;
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

        public Get(Trip trip, List<String> hashTag, List<String> imageUrlList) {
            this.tripId = trip.getId();
            this.title = trip.getTitle();
            this.content = trip.getContent();
            this.cost = trip.getCost();
            this.area = trip.getArea();
            this.address = trip.getAddress();
            this.placeName = trip.getPlaceName();
            this.x = String.valueOf(trip.getX().setScale(4, RoundingMode.HALF_UP));
            this.y = String.valueOf(trip.getY().setScale(4, RoundingMode.HALF_UP));
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
    @AllArgsConstructor
    public static class GetList {
        private Long tripId;
        private String title;
        private String nickname;
        private String thumbnailUrl;
        private String tripPeriod;
        private int viewCount;
        private LocalDate createdAt;

        public GetList(Trip trip, String thumbnailUrl) {
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
    public static class Delete {
        private boolean isDeleted;
    }

    @Getter
    @AllArgsConstructor
    public static class Result {
        private boolean isResult;
    }

    @Getter
    @AllArgsConstructor
    public static class Likes {
        private Long tripId;
        private String title;
        private String nickname;
        private String tripPeriod;

        public Likes(TripLike tripLike) {
            this.tripId = tripLike.getTrip().getId();
            this.title = tripLike.getTrip().getTitle();
            this.nickname = tripLike.getMember().getNickname();
            this.tripPeriod = Period.between(tripLike.getTrip().getTripStartDate(), tripLike.getTrip().getTripEndDate()).getDays() + "일";
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Scraps {
        private Long tripId;
        private String title;
        private String nickname;
        private String tripPeriod;

        public Scraps(TripScrap tripScrap) {
            this.tripId = tripScrap.getTrip().getId();
            this.title = tripScrap.getTrip().getTitle();
            this.nickname = tripScrap.getMember().getNickname();
            this.tripPeriod = Period.between(tripScrap.getTrip().getTripStartDate(), tripScrap.getTrip().getTripEndDate()).getDays() + "일";
        }
    }

    @Getter
    @AllArgsConstructor
    public static class GetByMember {
        private Long tripId;
        private String title;
        private String nickname;
        private String thumbnailUrl;
        private String tripPeriod;
        private int viewCount;
        private LocalDate createdAt;

        public GetByMember(Trip trip, String thumbnailUrl) {
            this.tripId = trip.getId();
            this.title = trip.getTitle();
            this.nickname = trip.getMember().getNickname();
            this.thumbnailUrl = thumbnailUrl;
            this.tripPeriod = Period.between(trip.getTripStartDate(), trip.getTripEndDate()).getDays() + "일";
            this.viewCount = trip.getViewCount();
            this.createdAt = trip.getCreatedAt().toLocalDate();
        }
    }
}
