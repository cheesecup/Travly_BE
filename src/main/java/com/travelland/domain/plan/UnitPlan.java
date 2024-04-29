package com.travelland.domain.plan;

import com.travelland.dto.plan.UnitPlanDto;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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

    private String time;

    private BigDecimal x;

    private BigDecimal y;

    @Column(length = 30)
    private String address;

    @Column(length = 30)
    private String placeName;

    private int budget;

    @Column(length = 300)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "day_plan_id")
    private DayPlan dayPlan;

    private Boolean isDeleted = false;

    public UnitPlan(UnitPlanDto.CreateAllInOne request, DayPlan dayPlan) {
        this.title = request.getTitle();
        this.time = request.getTime();
        this.x = request.getX();
        this.y = request.getY();
        this.address = request.getAddress();
        this.placeName = request.getPlaceName();
        this.budget = request.getBudget();
        this.content = request.getContent();
        this.dayPlan = dayPlan;

        checkTimeFormat();
    }

    public UnitPlan update(UnitPlanDto.UpdateAllInOne request) {
        this.title = request.getTitle();
        this.time = request.getTime();
        this.x = request.getX();
        this.y = request.getY();
        this.address = request.getAddress();
        this.placeName = request.getPlaceName();
        this.budget = request.getBudget();
        this.content = request.getContent();

        checkTimeFormat();

        return this;
    }

    public void delete() {
        this.isDeleted = true;
    }

    // 시간 형식이 HH:mm 포맷인지 확인
    public void checkTimeFormat() {
        Pattern pattern = Pattern.compile("([01]?[0-9]|2[0-3]):[0-5][0-9]");
        Matcher matcher = pattern.matcher(this.time);
        if (matcher.matches() == false) {
            throw new CustomException(ErrorCode.WRONG_TIME_FORMAT);
        }
    }
}
