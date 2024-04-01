package com.travelland.domain;

import com.travelland.dto.UnitPlanDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class UnitPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private int budget;

    private String location;

    private LocalDateTime time;

    @ManyToOne
    @JoinColumn(name = "day_plan_id")
    private DayPlan dayPlan;

    public UnitPlan(UnitPlanDto.CreateRequest request, DayPlan dayPlan) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.budget = request.getBudget();
        this.location = request.getLocation();
        this.time = request.getTime();
        this.dayPlan = dayPlan;
    }

    public UnitPlan update(UnitPlanDto.UpdateRequest request) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.budget = request.getBudget();
        this.location = request.getLocation();
        this.time = request.getTime();

        return this;
    }
}
