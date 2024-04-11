package com.travelland.domain.plan;

import com.travelland.dto.plan.DayPlanDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DayPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String title;

    @Column(length = 500)
    private String content;

    private int budget;

    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    private Boolean isDeleted = false;

    public DayPlan(DayPlanDto.Create request, Plan plan) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.budget = request.getBudget();
        this.date = request.getDate();
        this.plan = plan;
    }

    public DayPlan(DayPlanDto.CreateAllInOne request, Plan plan) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.budget = request.getBudget();
        this.date = request.getDate();
        this.plan = plan;
    }

    public DayPlan update(DayPlanDto.Update request) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.budget = request.getBudget();
        this.date = request.getDate();

        return this;
    }

    public DayPlan update(DayPlanDto.UpdateAllInOne request) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.budget = request.getBudget();
        this.date = request.getDate();

        return this;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
