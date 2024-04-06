package com.travelland.dto;

import com.travelland.domain.plan.UnitPlan;
import lombok.Getter;

import java.time.LocalDateTime;

public class UnitPlanDto {

    @Getter
    public static class CreateRequest {
        private String title;
        private String content;
        private int budget;
        private String location;
        private LocalDateTime time;
    }

    @Getter
    public static class CreateResponse {
        private Long unitPlanId;

        public CreateResponse(UnitPlan savedUnitPlan) {
            this.unitPlanId = savedUnitPlan.getId();
        }
    }

    @Getter
    public static class GetResponse {
        private Long unitPlanId;
        private String title;
        private String content;
        private int budget;
        private String location;
        private LocalDateTime time;

        public GetResponse(UnitPlan unitPlan) {
            this.unitPlanId = unitPlan.getId();
            this.title = unitPlan.getTitle();
            this.content = unitPlan.getContent();
            this.budget = unitPlan.getBudget();
            this.location = unitPlan.getLocation();
            this.time = unitPlan.getTime();
        }
    }

    @Getter
    public static class UpdateRequest {
        private String title;
        private String content;
        private int budget;
        private String location;
        private LocalDateTime time;
    }

    @Getter
    public static class UpdateResponse {
        private Long unitPlanId;

        public UpdateResponse(UnitPlan updatedUnitPlan) {
            this.unitPlanId = updatedUnitPlan.getId();
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
