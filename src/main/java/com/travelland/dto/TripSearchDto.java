package com.travelland.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.travelland.document.TripDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TripSearchDto {

    @Getter
    @AllArgsConstructor
    public static class CreateRequest {
        private Long tripId;
        private String title;
        private int cost;
        private String area;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate tripStartDate;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate tripEndDate;

        private GeoPoint location;
        private List<String> hashtag;
    }

    @Getter
    public static class GetResponse {
        private final String id;
        private final Long tripId;
        private final String title;
        private final int cost;
        private final String area;
        private final LocalDate tripStartDate;

        public GetResponse(TripDocument tripDocument) {
            this.id = tripDocument.getId();
            this.tripId = tripDocument.getTripId();
            this.title = tripDocument.getTitle();
            this.cost = tripDocument.getCost();
            this.area = tripDocument.getArea();
            this.tripStartDate = tripDocument.getTripStartDate();
        }
    }
    @Getter
    @AllArgsConstructor
    @Builder
    public static class RankResponse {
        private final String key;
        private final Long count;
        private final String status;
        private final int value;
    }
}
