package com.travelland.dto.plan;

import com.travelland.valid.plan.PlanValidationGroups;
import com.travelland.domain.plan.UnitPlan;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

public class UnitPlanDto {

    @Getter
    public static class CreateAllInOne {
        @Size(max = 100)
        private String title;
        @NotBlank(message = "시간을 입력해 주세요.", groups = PlanValidationGroups.TimeBlankGroup.class)
        @Pattern(message = "시간 형식은 HH:mm이어야 합니다.", regexp = "([01]?[0-9]|2[0-3]):[0-5][0-9]", groups = PlanValidationGroups.TimeBlankGroup.class)
        private String time;
        private BigDecimal x;
        private BigDecimal y;
        @Size(max = 30)
        private String address;
        private String placeName;
        private int budget;
        @Size(max = 300)
        private String content;
    }

    @Getter
    public static class GetAllInOne {
        private final Long unitPlanId;
        private final String title;
        private final String time;
        private final BigDecimal x;
        private final BigDecimal y;
        private final String address;
        private String placeName;
        private final int budget;
        private final String content;

        public GetAllInOne(UnitPlan unitPlan) {
            this.unitPlanId = unitPlan.getId();
            this.title = unitPlan.getTitle();
            this.time = unitPlan.getTime();
            this.x = unitPlan.getX();
            this.y = unitPlan.getY();
            this.address = unitPlan.getAddress();
            this.placeName = unitPlan.getPlaceName();
            this.budget = unitPlan.getBudget();
            this.content = unitPlan.getContent();
        }
    }

    @Getter
    public static class UpdateAllInOne {
        private Long unitPlanId;
        @Size(max = 100)
        private String title;
        @NotBlank(message = "시간을 입력해 주세요.", groups = PlanValidationGroups.TimeBlankGroup.class)
        @Pattern(message = "시간 형식은 HH:mm이어야 합니다.", regexp = "([01]?[0-9]|2[0-3]):[0-5][0-9]", groups = PlanValidationGroups.TimeBlankGroup.class)
        private String time;
        private BigDecimal x;
        private BigDecimal y;
        @Size(max = 30)
        private String address;
        private String placeName;
        private int budget;
        @Size(max = 300)
        private String content;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Delete {
        private final boolean isDeleted;
    }
}
