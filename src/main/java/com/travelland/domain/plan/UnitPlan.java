package com.travelland.domain.plan;

import com.travelland.dto.plan.UnitPlanDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UnitPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String title;

    @Column(length = 300)
    private String content;

    private int budget;

    @Column(length = 30)
    private String address;

    private BigDecimal x;

    private BigDecimal y;

    private LocalDateTime time;

    @ManyToOne
    @JoinColumn(name = "day_plan_id")
    private DayPlan dayPlan;

    private Boolean isDeleted = false;

    public UnitPlan(UnitPlanDto.Create request, DayPlan dayPlan) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.budget = request.getBudget();
        this.address = request.getAddress();
        this.x = request.getX();
        this.y = request.getY();
        this.dayPlan = dayPlan;
    }

    public UnitPlan(UnitPlanDto.CreateAllInOne request, DayPlan dayPlan) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.budget = request.getBudget();
        this.address = request.getAddress();
        this.x = request.getX();
        this.y = request.getY();
        this.dayPlan = dayPlan;
    }

    public UnitPlan update(UnitPlanDto.Update request) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.budget = request.getBudget();
        this.address = request.getAddress();
        this.x = request.getX();
        this.y = request.getY();
        this.time = request.getTime();

        return this;
    }

    public UnitPlan update(UnitPlanDto.UpdateAllInOne request) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.budget = request.getBudget();
        this.address = request.getAddress();
        this.x = request.getX();
        this.y = request.getY();
        this.time = request.getTime();

        return this;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
