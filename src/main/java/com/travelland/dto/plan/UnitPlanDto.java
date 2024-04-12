package com.travelland.dto.plan;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.travelland.controller.valid.PlanValidationGroups;
import com.travelland.domain.plan.UnitPlan;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class UnitPlanDto {

    @Getter
    public static class Create {
        //쓰기싫을수도 //@NotBlank(message = "제목을 입력해주세요.", groups = PlanValidationGroups.TitleBlankGroup.class)
        @Size(max = 100)
        private String title;
        //쓰기싫을수도 //@NotBlank(message = "내용을 입력해주세요,", groups = PlanValidationGroups.ContentBlankGroup.class)
        @Size(max = 300)
        private String content;
        //FE에없음 //@Min(value = 0, message = "비용은 최소 0원 이상입니다.", groups = PlanValidationGroups.CostRangeGroup.class)
        private int budget;
        //쓰기싫을수도 //@NotBlank(message = "주소를 입력해 주세요.", groups = PlanValidationGroups.AddressBlankGroup.class)
        @Size(max = 30)
        private String address;
        private String placeName;
        private BigDecimal x;
        private BigDecimal y;
        @NotBlank(message = "시간을 입력해 주세요.", groups = PlanValidationGroups.TimeBlankGroup.class)
        @Pattern(message = "시간 형식은 HH:mm이어야 합니다.", regexp = "([01]?[0-9]|2[0-3]):[0-5][0-9]", groups = PlanValidationGroups.TimeBlankGroup.class)
        private String time;
    }

    @Getter
    public static class CreateAllInOne {
        //@NotBlank(message = "제목을 입력해주세요.", groups = PlanValidationGroups.TitleBlankGroup.class)
        @Size(max = 100)
        private String title;
        //@NotBlank(message = "내용을 입력해주세요,", groups = PlanValidationGroups.ContentBlankGroup.class)
        @Size(max = 300)
        private String content;
        //@Min(value = 0, message = "비용은 최소 0원 이상입니다.", groups = PlanValidationGroups.CostRangeGroup.class)
        private int budget;
        //@NotBlank(message = "주소를 입력해 주세요.", groups = PlanValidationGroups.AddressBlankGroup.class)
        @Size(max = 30)
        private String address;
        private String placeName;
        private BigDecimal x;
        private BigDecimal y;
        @NotBlank(message = "시간을 입력해 주세요.", groups = PlanValidationGroups.TimeBlankGroup.class)
        @Pattern(message = "시간 형식은 HH:mm이어야 합니다.", regexp = "([01]?[0-9]|2[0-3]):[0-5][0-9]", groups = PlanValidationGroups.TimeBlankGroup.class)
        private String time;
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
        private String placeName;
        private final BigDecimal x;
        private final BigDecimal y;
        private final String time;

        public Get(UnitPlan unitPlan) {
            this.unitPlanId = unitPlan.getId();
            this.title = unitPlan.getTitle();
            this.content = unitPlan.getContent();
            this.budget = unitPlan.getBudget();
            this.address = unitPlan.getAddress();
            this.placeName = unitPlan.getPlaceName();
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
        private String placeName;
        private final BigDecimal x;
        private final BigDecimal y;
        private final String time;

        public GetAllInOne(UnitPlan unitPlan) {
            this.unitPlanId = unitPlan.getId();
            this.title = unitPlan.getTitle();
            this.content = unitPlan.getContent();
            this.budget = unitPlan.getBudget();
            this.address = unitPlan.getAddress();
            this.placeName = unitPlan.getPlaceName();
            this.x = unitPlan.getX();
            this.y = unitPlan.getY();
            this.time = unitPlan.getTime();
        }
    }

    @Getter
    public static class Update {
        //@NotBlank(message = "제목을 입력해주세요.", groups = PlanValidationGroups.TitleBlankGroup.class)
        @Size(max = 100)
        private String title;
        //@NotBlank(message = "내용을 입력해주세요,", groups = PlanValidationGroups.ContentBlankGroup.class)
        @Size(max = 300)
        private String content;
        //@Min(value = 0, message = "비용은 최소 0원 이상입니다.", groups = PlanValidationGroups.CostRangeGroup.class)
        private int budget;
        //@NotBlank(message = "주소를 입력해 주세요.", groups = PlanValidationGroups.AddressBlankGroup.class)
        @Size(max = 30)
        private String address;
        private String placeName;
        private BigDecimal x;
        private BigDecimal y;
        @NotBlank(message = "시간을 입력해 주세요.", groups = PlanValidationGroups.TimeBlankGroup.class)
        @Pattern(message = "시간 형식은 HH:mm이어야 합니다.", regexp = "([01]?[0-9]|2[0-3]):[0-5][0-9]", groups = PlanValidationGroups.TimeBlankGroup.class)
        private String time;
    }

    @Getter
    public static class UpdateAllInOne {
        private Long unitPlanId;
        //@NotBlank(message = "제목을 입력해주세요.", groups = PlanValidationGroups.TitleBlankGroup.class)
        @Size(max = 100)
        private String title;
        //@NotBlank(message = "내용을 입력해주세요,", groups = PlanValidationGroups.ContentBlankGroup.class)
        @Size(max = 300)
        private String content;
        //@Min(value = 0, message = "비용은 최소 0원 이상입니다.", groups = PlanValidationGroups.CostRangeGroup.class)
        private int budget;
        //@NotBlank(message = "주소를 입력해 주세요.", groups = PlanValidationGroups.AddressBlankGroup.class)
        @Size(max = 30)
        private String address;
        private String placeName;
        private BigDecimal x;
        private BigDecimal y;
        @NotBlank(message = "시간을 입력해 주세요.", groups = PlanValidationGroups.TimeBlankGroup.class)
        @Pattern(message = "시간 형식은 HH:mm이어야 합니다.", regexp = "([01]?[0-9]|2[0-3]):[0-5][0-9]", groups = PlanValidationGroups.TimeBlankGroup.class)
        private String time;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Delete {
        private final boolean isDeleted;
    }
}
