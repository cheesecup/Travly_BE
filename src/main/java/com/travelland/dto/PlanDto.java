package com.travelland.dto;

import com.travelland.domain.plan.Plan;
import com.travelland.domain.plan.PlanLike;
import com.travelland.domain.plan.PlanScrap;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

public class PlanDto {

    @Getter
    @AllArgsConstructor
    public static class Create {
        private String title;
        private String content;
        private int budget;
        private String area;
        private boolean isPublic;
        private LocalDate tripStartDate;
        private LocalDate tripEndDate;
        private boolean isVotable;
    }

    @Getter
    public static class Id {
        private final Long planId;
        public Id(Plan savedPlan) {
            this.planId = savedPlan.getId();
        }
    }

    @Getter
    public static class Read {
        private final Long planId;
        private final String title;
        private final String content;
        private final int budget;
        private final String area;
        private final boolean isPublic;
        private final LocalDate tripStartDate;
        private final LocalDate tripEndDate;
        private final int viewCount;
        private final int likeCount;
        private final boolean isVotable;
        private final LocalDateTime createdAt;
        private final String memberNickname;

        public Read(Plan plan) {
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
    @AllArgsConstructor
    public static class Update {
        private String title;
        private String content;
        private int budget;
        private String area;
        private boolean isPublic;
        private LocalDate tripStartDate;
        private LocalDate tripEndDate;
        private boolean isVotable;
    }

    @Getter
    public static class Delete {
        private final boolean isDeleted;
        public Delete(boolean result) {
            this.isDeleted = result;
        }
    }

    @Getter
    public static class Result {
        private final boolean result;
        public Result(boolean result) {
            this.result = result;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Likes {
        private Long planId;
        private String title;
        private String nickname;
        private String planPeriod;

        public Likes(PlanLike planLike){
            this.planId = planLike.getPlan().getId();
            this.title = planLike.getPlan().getTitle();
            this.nickname = planLike.getMember().getNickname();
            this.planPeriod = Period.between(planLike.getPlan().getTripStartDate(),
                            planLike.getPlan().getTripEndDate()).getDays() + "일";
        }
    }
    @Getter
    @AllArgsConstructor
    public static class Scraps {
        private Long planId;
        private String title;
        private String nickname;
        private String planPeriod;

        public Scraps(PlanScrap planScrap){
            this.planId = planScrap.getPlan().getId();
            this.title = planScrap.getPlan().getTitle();
            this.nickname = planScrap.getMember().getNickname();
            this.planPeriod = Period.between(planScrap.getPlan().getTripStartDate(),
                                    planScrap.getPlan().getTripEndDate()).getDays() + "일";
        }
    }
}
