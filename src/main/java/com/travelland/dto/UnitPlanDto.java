package com.travelland.dto;

import com.travelland.domain.plan.UnitPlan;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UnitPlanDto {

    @Getter
    public static class Create {
        private String title;
        private String content;
        private int budget;
        private String address;
        private BigDecimal x;
        private BigDecimal y;
        private LocalDateTime time;
    }

    @Getter
    public static class CreateAllInOne {
        private String title;
        private String content;
        private int budget;
        private String address;
        private BigDecimal x;
        private BigDecimal y;
        private LocalDateTime time;
    }

    @Getter
    public static class Id {
        private final Long unitPlanId;

        public Id(UnitPlan savedUnitPlan) {
            this.unitPlanId = savedUnitPlan.getId();
        }
    }

    @Getter
    public static class Get {
        private final Long unitPlanId;
        private final String title;
        private final String content;
        private final int budget;
        private final String address;
        private final BigDecimal x;
        private final BigDecimal y;
        private final LocalDateTime time;

        public Get(UnitPlan unitPlan) {
            this.unitPlanId = unitPlan.getId();
            this.title = unitPlan.getTitle();
            this.content = unitPlan.getContent();
            this.budget = unitPlan.getBudget();
            this.address = unitPlan.getAddress();
            this.x = unitPlan.getX();
            this.y = unitPlan.getY();
            this.time = unitPlan.getTime();
        }
    }

    @Getter
    public static class GetAllInOne {
        private final Long unitPlanId;
        private final String title;
        private final String content;
        private final int budget;
        private final String address;
        private final BigDecimal x;
        private final BigDecimal y;
        private final LocalDateTime time;

        public GetAllInOne(UnitPlan unitPlan) {
            this.unitPlanId = unitPlan.getId();
            this.title = unitPlan.getTitle();
            this.content = unitPlan.getContent();
            this.budget = unitPlan.getBudget();
            this.address = unitPlan.getAddress();
            this.x = unitPlan.getX();
            this.y = unitPlan.getY();
            this.time = unitPlan.getTime();
        }
    }

    @Getter
    public static class Update {
        private String title;
        private String content;
        private int budget;
        private String address;
        private BigDecimal x;
        private BigDecimal y;
        private LocalDateTime time;
    }

    @Getter
    public static class UpdateAllInOne {
        private Long unitPlanId;
        private String title;
        private String content;
        private int budget;
        private String address;
        private BigDecimal x;
        private BigDecimal y;
        private LocalDateTime time;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Delete {
        private final boolean isDeleted;
    }
}
