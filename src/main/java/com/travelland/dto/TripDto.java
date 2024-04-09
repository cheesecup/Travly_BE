package com.travelland.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.travelland.document.TripSearchDoc;
import com.travelland.domain.trip.Trip;
import com.travelland.domain.trip.TripLike;
import com.travelland.domain.trip.TripScrap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

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
        private String address; //서울 동작구
        private Boolean isPublic;
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
        private Boolean isPublic;
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
        private String address;
        private LocalDate tripStartDate;
        private LocalDate tripEndDate;
        private int viewCount;
        private int likeCount;
        private String nickname;
        private LocalDate createdAt;

        private List<String> hashTag;
        private List<String> imageUrlList;

        private Boolean isLike;
        private Boolean isScrap;

        public Get(Trip trip, List<String> hashTag, List<String> imageUrlList, boolean isLike, boolean isScrap) {
            this.tripId = trip.getId();
            this.title = trip.getTitle();
            this.content = trip.getContent();
            this.cost = trip.getCost();
            this.address = trip.getAddress();
            this.tripStartDate = trip.getTripStartDate();
            this.tripEndDate = trip.getTripEndDate();
            this.viewCount = trip.getViewCount();
            this.likeCount = trip.getLikeCount();
            this.nickname = trip.getMember().getNickname();
            this.createdAt = trip.getCreatedAt().toLocalDate();
            this.hashTag = hashTag;
            this.imageUrlList = imageUrlList;
            this.isLike = isLike;
            this.isScrap = isScrap;
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

        public GetList(TripSearchDoc trip, String thumbnailUrl) {
            this.tripId = trip.getTripId();
            this.title = trip.getTitle();
            this.nickname = trip.getNickname();
            this.thumbnailUrl = thumbnailUrl;
            this.tripPeriod = betweenPeriod(trip.getTripStartDate(), trip.getTripEndDate());
            this.viewCount = trip.getViewCount();
            this.createdAt = trip.getCreatedAt().toLocalDate();
        }

        private String betweenPeriod(LocalDate startDate, LocalDate endDate) {
            int days = Period.between(startDate, endDate).getDays();
            return (days == 0) ?  "하루" : days + "박 " + (days + 1) + "일";
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Delete {
        private Boolean isDeleted;
    }

    @Getter
    @AllArgsConstructor
    public static class Result {
        private Boolean isResult;
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

    @Getter
    public static class Search {
        private final String id;
        private final Long tripId;
        private final String title;
        private final String address;
        private final String content;
        private final String nickname;
        private final String profileUrl;

        public Search(TripSearchDoc tripSearchDoc) {
            this.id = tripSearchDoc.getId();
            this.tripId = tripSearchDoc.getTripId();
            this.address = tripSearchDoc.getAddress();
            this.title = tripSearchDoc.getTitle();
            this.content = tripSearchDoc.getContent();
            this.nickname = tripSearchDoc.getNickname();
            this.profileUrl = tripSearchDoc.getProfileUrl();
        }
    }

    @Getter
    @Builder
    public static class SearchResult {
        private final List<Search> searches;
        private final long totalCount;
        private final String resultAddress;
        private final List<String> nearPlaces;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Rank {
        private final String key;
        private final Long count;
        private final String status;
        private final int value;
    }
}
