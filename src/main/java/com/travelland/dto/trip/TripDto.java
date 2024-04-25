package com.travelland.dto.trip;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.travelland.domain.member.Member;
import com.travelland.domain.trip.Trip;
import com.travelland.domain.trip.TripLike;
import com.travelland.esdoc.TripSearchDoc;
import com.travelland.valid.trip.TripValidationGroups;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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

        @NotBlank(message = "제목을 입력해주세요.", groups = TripValidationGroups.TitleBlankGroup.class)
        private String title;

        @NotBlank(message = "내용을 입력해주세요,", groups = TripValidationGroups.ContentBlankGroup.class)
        private String content;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate tripStartDate;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate tripEndDate;

        @Min(value = 0, message = "비용은 최소 0원 이상입니다.", groups = TripValidationGroups.CostRangeGroup.class)
        private Integer cost;
        private List<String> hashTag;

        @NotBlank(message = "도로명 주소를 입력해 주세요.", groups = TripValidationGroups.AddressBlankGroup.class)
        private String address;
        
        private String placeName; //여행 장소

        private Boolean isPublic;
    }

    @Getter
    @AllArgsConstructor
    public static class Update {

        @NotBlank(message = "제목을 입력해주세요.", groups = TripValidationGroups.TitleBlankGroup.class)
        private String title;

        @NotBlank(message = "내용을 입력해주세요,", groups = TripValidationGroups.ContentBlankGroup.class)
        private String content;

        private List<String> hashTag;

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

        private Long tripId;
        private String title;
        private String content;
        private int cost;
        private String area;
        private LocalDate tripStartDate;
        private LocalDate tripEndDate;

        private List<String> hashtagList;
        private List<String> imageUrlList;

        private Boolean isLike;
        private Boolean isScrap;
        private Boolean isWriter;

        private String nickname;
        private String profileImage;

        public Get(Trip trip, Member member, List<String> hashtagList, List<String> imageUrlList, boolean isLike, boolean isScrap, boolean isWriter) {
            this.tripId = trip.getId();
            this.title = trip.getTitle();
            this.content = trip.getContent();
            this.cost = trip.getCost();
            this.area = trip.getArea();
            this.tripStartDate = trip.getTripStartDate();
            this.tripEndDate = trip.getTripEndDate();
            this.hashtagList = hashtagList;
            this.imageUrlList = imageUrlList;
            this.isLike = isLike;
            this.isScrap = isScrap;
            this.isWriter = isWriter;
            this.nickname = member.getNickname();
            this.profileImage = member.getProfileImage();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class GetList {
        private Long tripId;
        private String area;
        private String title;
        private LocalDate tripStartDate;
        private LocalDate tripEndDate;
        private String thumbnailUrl;
        private List<String> hashtagList;
        private Boolean isScrap;

        public GetList(TripSearchDoc trip) {
            this.tripId = trip.getTripId();
            this.area = trip.getArea();
            this.title = trip.getTitle();
            this.tripStartDate = trip.getTripStartDate();
            this.tripEndDate = trip.getTripEndDate();
            this.hashtagList = trip.getHashtag();
            this.thumbnailUrl = trip.getThumbnailUrl();
            this.isScrap = false;
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
        private String area;
        private LocalDate tripStartDate;
        private LocalDate tripEndDate;
        private List<String> hashtagList;
        private String thumbnailUrl;

        public Scraps(TripSearchDoc trip) {
            this.tripId = trip.getTripId();
            this.title = trip.getTitle();
            this.area = trip.getArea();
            this.tripStartDate = trip.getTripStartDate();
            this.tripEndDate = trip.getTripEndDate();
            this.hashtagList = trip.getHashtag();
            this.thumbnailUrl = trip.getThumbnailUrl();
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
        private final String area;
        private final String thumbnailUrl;
        private final String content;
        private final String placeName;
        private final LocalDate tripStartDate;
        private final LocalDate tripEndDate;
        private final List<String> hashtagList;

        public Search(TripSearchDoc tripSearchDoc) {
            this.id = tripSearchDoc.getId();
            this.tripId = tripSearchDoc.getTripId();
            this.area = tripSearchDoc.getArea();
            this.thumbnailUrl = tripSearchDoc.getThumbnailUrl();
            this.title = tripSearchDoc.getTitle();
            this.content = tripSearchDoc.getContent();
            this.placeName = tripSearchDoc.getPlaceName();
            this.tripStartDate = tripSearchDoc.getTripStartDate();
            this.tripEndDate = tripSearchDoc.getTripEndDate();
            this.hashtagList = tripSearchDoc.getHashtag();
        }
    }

    @Getter
    public static class Top10 {
        private final String id;
        private final Long tripId;
        private final String title;
        private final String area;
        private final String content;
        private final String placeName;
        private final String thumbnailUrl;
        private final LocalDate tripStartDate;
        private final LocalDate tripEndDate;
        private final List<String> hashtagList;

        public Top10(TripSearchDoc tripSearchDoc) {
            this.id = tripSearchDoc.getId();
            this.tripId = tripSearchDoc.getTripId();
            this.area = tripSearchDoc.getArea();
            this.title = tripSearchDoc.getTitle();
            this.thumbnailUrl = tripSearchDoc.getThumbnailUrl();
            this.content = subContent(tripSearchDoc.getContent());
            this.placeName = tripSearchDoc.getPlaceName();
            this.tripStartDate = tripSearchDoc.getTripStartDate();
            this.tripEndDate = tripSearchDoc.getTripEndDate();
            this.hashtagList = tripSearchDoc.getHashtag();
        }
        private String subContent(String origin){
            return origin.length() < 16 ? origin : origin.substring(0,15);
        }
    }

    @Getter
    @Builder
    public static class SearchResult {
        private final List<Search> searches;
        private final long totalCount;
        private final String resultKeyword;
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
