package com.travelland.dto;

import com.travelland.domain.plan.DayPlan;
import lombok.Getter;

import java.time.LocalDateTime;

public class DayPlanDto {

    @Getter
    public static class CreateRequest {
        private String title;
        private String content;
        private int budget;
        private LocalDateTime date;
    }

    @Getter
    public static class CreateResponse {
        private Long dayPlanId;

        public CreateResponse(DayPlan savedDayPlan) {
            this.dayPlanId = savedDayPlan.getId();
        }
    }

    @Getter
    public static class GetResponse {
        private Long dayPlanId;
        private String title;
        private String content;
        private int budget;
        private LocalDateTime date;

        public GetResponse(DayPlan dayPlan) {
            this.dayPlanId = dayPlan.getId();
            this.title = dayPlan.getTitle();
            this.content = dayPlan.getContent();
            this.budget = dayPlan.getBudget();
            this.date = dayPlan.getDate();
        }
    }

    @Getter
    public static class UpdateRequest {
        private String title;
        private String content;
        private int budget;
        private LocalDateTime date;
    }

    @Getter
    public static class UpdateResponse {
        private Long dayPlanId;

        public UpdateResponse(DayPlan updatedDayPlan) {
            this.dayPlanId = updatedDayPlan.getId();
        }
    }

    @Getter
    public static class DeleteResponse {
        private boolean isDeleted;

        public DeleteResponse(boolean result) {
            this.isDeleted = result;
        }
    }
}
