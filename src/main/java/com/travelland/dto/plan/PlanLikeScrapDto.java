package com.travelland.dto.plan;

import com.travelland.domain.plan.Plan;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class PlanLikeScrapDto {

    @Getter
    @AllArgsConstructor
    public static class GetList {
        private final Long planId;
        private final String title;
        private final LocalDateTime createdAt;
        private final int viewCount;

        public GetList(Plan plan) {
            this.planId = plan.getId();
            this.title = plan.getTitle();
            this.createdAt = plan.getCreatedAt();
            this.viewCount = plan.getViewCount();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class GetLists {
        private List<GetList> listList;
        private Long totalCount;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Result {
        private final Boolean result;
    }
}
