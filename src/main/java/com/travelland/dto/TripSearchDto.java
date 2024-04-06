package com.travelland.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.travelland.document.TripSearchDoc;
import com.travelland.domain.trip.Trip;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.math.RoundingMode;
import java.time.LocalDate;
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

        private String address;
        private String placeName;
        private String x;
        private String y;

        public CreateRequest(Trip trip, List<String> hashtag) {
            this.tripId = trip.getId();
            this.title = trip.getTitle();
            this.cost = trip.getCost();
            this.area = trip.getArea();
            this.tripStartDate = trip.getTripStartDate();
            this.tripEndDate = trip.getTripEndDate();
            this.hashtag = hashtag;
            this.address = trip.getAddress();
            this.placeName = trip.getPlaceName();
            this.x = String.valueOf(trip.getX().setScale(4, RoundingMode.HALF_UP));
            this.y = String.valueOf(trip.getY().setScale(4, RoundingMode.HALF_UP));
        }
    }

    @Getter
    public static class GetResponse {
        private final String id;
        private final Long tripId;
        private final String title;
        private final int cost;
        private final String area;
        private final LocalDate tripStartDate;

        public GetResponse(TripSearchDoc tripSearchDoc) {
            this.id = tripSearchDoc.getId();
            this.tripId = tripSearchDoc.getTripId();
            this.title = tripSearchDoc.getTitle();
            this.cost = tripSearchDoc.getCost();
            this.area = tripSearchDoc.getArea();
            this.tripStartDate = tripSearchDoc.getTripStartDate();
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
