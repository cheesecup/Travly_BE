package com.travelland.dto.plan;

import com.travelland.domain.plan.Plan;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PlanToTripDto {
    private final String title;
    private final String content;
    private final int cost;
    private final String area;
    private final LocalDate tripStartDate;
    private final LocalDate tripEndDate;

    @Builder
    public PlanToTripDto(Plan plan, String content) {
        this.title = plan.getTitle();
        this.content = content;
        this.cost = plan.getBudget();
        this.area = plan.getArea();
        this.tripStartDate = plan.getTripStartDate();
        this.tripEndDate = plan.getTripEndDate();
    }
}