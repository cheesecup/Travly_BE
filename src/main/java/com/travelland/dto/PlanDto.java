package com.travelland.dto;

import com.travelland.domain.Plan;
import lombok.Getter;

import java.time.LocalDateTime;

public class PlanDto {

    @Getter
    public static class CreateRequest {
        private String title;
        private String content;
        private int budget;
        private String area;
        private boolean isPublic;
        private LocalDateTime tripStartDate;
        private LocalDateTime tripEndDate;
        private boolean isVotable;
    }

    @Getter
    public static class CreateResponse {
        private Long planId;

        public CreateResponse(Plan savedPlan) {
            this.planId = savedPlan.getId();
        }
    }

    @Getter
    public static class ReadResponse {
        private Long planId;
        private String title;
        private String content;
        private int budget;
        private String area;
        private boolean isPublic;
        private LocalDateTime tripStartDate;
        private LocalDateTime tripEndDate;
        private int viewCount;
        private int likeCount;
        private boolean isVotable;
        private LocalDateTime createdAt;
        private String memberNickname;

        public ReadResponse(Plan plan) {
            this.planId = plan.getId();
            this.title = plan.getTitle();
            this.content = plan.getContent();
            this.budget = plan.getBudget();
            this.area = plan.getArea();
            this.isPublic = plan.isPublic();
            this.tripStartDate = plan.getTripStartDate();
            this.tripEndDate = plan.getTripEndDate();
            this.viewCount = plan.getViewCount();
            this.likeCount = plan.getViewCount();
            this.isVotable = plan.isVotable();
            this.createdAt = plan.getCreatedAt();
            this.memberNickname = plan.getMember().getNickname();
        }
    }

    @Getter
    public static class UpdateRequest {
        private String title;
        private String content;
        private int budget;
        private String area;
        private boolean isPublic;
        private LocalDateTime tripStartDate;
        private LocalDateTime tripEndDate;
        private boolean isVotable;
    }

    @Getter
    public static class UpdateResponse {
        private Long planId;

        public UpdateResponse(Plan updatedPlan) {
            this.planId = updatedPlan.getId();
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
