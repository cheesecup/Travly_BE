package com.travelland.domain;

import com.travelland.dto.DayPlanDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor
public class DayPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private int budget;

    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    public DayPlan(DayPlanDto.CreateRequest request, Plan plan) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.budget = request.getBudget();
        this.date = request.getDate();
        this.plan = plan;
    }

    public DayPlan update(DayPlanDto.UpdateRequest request) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.budget = request.getBudget();
        this.date = request.getDate();

        return this;
    }
}
