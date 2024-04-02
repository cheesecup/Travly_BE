package com.travelland.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.travelland.document.TripDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class TripSearchDto {

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

    }

    @Getter
    public static class GetResponse {
        private final String tripId;
        private final String title;
        private final String content;
        private final int cost;
        private final String area;
        private final LocalDate tripStartDate;

        public GetResponse(TripDocument tripDocument) {
            this.tripId = tripDocument.getId();
            this.title = tripDocument.getTitle();
            this.content = tripDocument.getContent();
            this.cost = tripDocument.getCost();
            this.area = tripDocument.getArea();
            this.tripStartDate = tripDocument.getTripStartDate();
        }
    }
}
