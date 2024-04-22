package com.travelland.dto.plan;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.travelland.domain.plan.DayPlan;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

public class DayPlanDto {

    @Getter
    public static class Create {
//        @NotBlank(message = "제목을 입력해주세요.", groups = PlanValidationGroups.TitleBlankGroup.class)
//        @Size(max = 100)
//        private String title;
//        @NotBlank(message = "내용을 입력해주세요,", groups = PlanValidationGroups.ContentBlankGroup.class)
//        @Size(max = 500)
//        private String content;
//        @Min(value = 0, message = "비용은 최소 0원 이상입니다.", groups = PlanValidationGroups.CostRangeGroup.class)
//        private int budget;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate date;
    }

    @Getter
    public static class CreateAllInOne {
//        @NotBlank(message = "제목을 입력해주세요.", groups = PlanValidationGroups.TitleBlankGroup.class)
//        @Size(max = 100)
//        private String title;
//        @NotBlank(message = "내용을 입력해주세요,", groups = PlanValidationGroups.ContentBlankGroup.class)
//        @Size(max = 500)
//        private String content;
//        @Min(value = 0, message = "비용은 최소 0원 이상입니다.", groups = PlanValidationGroups.CostRangeGroup.class)
//        private int budget;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate date;
        private List<UnitPlanDto.CreateAllInOne> unitPlans;
    }

    @Getter
    public static class Id {
        private final Long dayPlanId;

        public Id(DayPlan savedDayPlan) {
            this.dayPlanId = savedDayPlan.getId();
        }
    }

    @Getter
    public static class Get {
        private final Long dayPlanId;
//        private final String title;
//        private final String content;
//        private final int budget;
        private final LocalDate date;

        public Get(DayPlan dayPlan) {
            this.dayPlanId = dayPlan.getId();
//            this.title = dayPlan.getTitle();
//            this.content = dayPlan.getContent();
//            this.budget = dayPlan.getBudget();
            this.date = dayPlan.getDate();
        }
    }

    @Getter
    @ToString
    public static class GetAllInOne {
        private final Long dayPlanId;
//        private final String title;
//        private final String content;
//        private final int budget;
        private final LocalDate date;
        private final List<UnitPlanDto.GetAllInOne> unitPlans;
        private final String startAddress; // 당일의 첫번재 UnitPlan 장소
        private final String endAddress;   // 당일의 마지막 UnitPlan 장소
        private final String path;         // 당일의 첫번째~마지막 모든 경로

        @Builder
        public GetAllInOne(Get dayPlan, List<UnitPlanDto.GetAllInOne> unitPlans, String startAddress, String endAddress, String path) {
            this.dayPlanId = dayPlan.getDayPlanId();
//            this.title = dayPlan.getTitle();
//            this.content = dayPlan.getContent();
//            this.budget = dayPlan.getBudget();
            this.date = dayPlan.getDate();
            this.unitPlans = unitPlans;
            this.startAddress = startAddress;
            this.endAddress = endAddress;
            this.path = path;
        }
    }

    @Getter
    public static class Update {
//        @NotBlank(message = "제목을 입력해주세요.", groups = PlanValidationGroups.TitleBlankGroup.class)
//        @Size(max = 100)
//        private String title;
//        @NotBlank(message = "내용을 입력해주세요,", groups = PlanValidationGroups.ContentBlankGroup.class)
//        @Size(max = 500)
//        private String content;
//        @Min(value = 0, message = "비용은 최소 0원 이상입니다.", groups = PlanValidationGroups.CostRangeGroup.class)
//        private int budget;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate date;
    }

    @Getter
    public static class UpdateAllInOne {
        private Long dayPlanId;
//        @NotBlank(message = "제목을 입력해주세요.", groups = PlanValidationGroups.TitleBlankGroup.class)
//        @Size(max = 100)
//        private String title;
//        @NotBlank(message = "내용을 입력해주세요,", groups = PlanValidationGroups.ContentBlankGroup.class)
//        @Size(max = 500)
//        private String content;
//        @Min(value = 0, message = "비용은 최소 0원 이상입니다.", groups = PlanValidationGroups.CostRangeGroup.class)
//        private int budget;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate date;
        private List<UnitPlanDto.UpdateAllInOne> unitPlans;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Delete {
        private final boolean isDeleted;
    }
}
