package com.travelland.domain.plan;

import com.travelland.dto.plan.UnitPlanDto;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Column(length = 30)
    private String placeName;

    private BigDecimal x;

    private BigDecimal y;

    private String time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "day_plan_id")
    private DayPlan dayPlan;

    private Boolean isDeleted = false;

//    public void setDayPlan(DayPlan dayPlan) {
//        this.dayPlan = dayPlan;
//    }

    public UnitPlan(UnitPlanDto.Create request, DayPlan dayPlan) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.budget = request.getBudget();
        this.address = request.getAddress();
        this.placeName = request.getPlaceName();
        this.x = request.getX();
        this.y = request.getY();
        this.time = request.getTime();
        this.dayPlan = dayPlan;
    }

    public UnitPlan(UnitPlanDto.CreateAllInOne request, DayPlan dayPlan) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.budget = request.getBudget();
        this.address = request.getAddress();
        this.placeName = request.getPlaceName();
        this.x = request.getX();
        this.y = request.getY();
        this.time = request.getTime();
        this.dayPlan = dayPlan;
    }

    public UnitPlan update(UnitPlanDto.Update request) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.budget = request.getBudget();
        this.address = request.getAddress();
        this.placeName = request.getPlaceName();
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
        this.placeName = request.getPlaceName();
        this.x = request.getX();
        this.y = request.getY();
        this.time = request.getTime();

        return this;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void checkTimeFormat() {
        String timePattern = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
        Pattern pattern = Pattern.compile(timePattern);
        Matcher matcher = pattern.matcher(this.time);

        if (matcher.matches() == false) {
            throw new CustomException(ErrorCode.WRONG_TIMEFORMAT);
        }
    }
}
