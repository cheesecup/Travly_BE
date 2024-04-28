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
    public static class CreateAllInOne {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate date;
        private List<UnitPlanDto.CreateAllInOne> unitPlans;
    }

    @Getter
    @ToString
    public static class GetAllInOne {
        private final Long dayPlanId;
        private final LocalDate date;
        private final List<UnitPlanDto.GetAllInOne> unitPlans;
        private final String startAddress; // 당일의 첫번재 UnitPlan 장소
        private final String endAddress;   // 당일의 마지막 UnitPlan 장소
        private final String path;         // 당일의 첫번째~마지막 모든 경로

        @Builder
        public GetAllInOne(DayPlan dayPlan, List<UnitPlanDto.GetAllInOne> unitPlans, String startAddress, String endAddress, String path) {
            this.dayPlanId = dayPlan.getId();
            this.date = dayPlan.getDate();
            this.unitPlans = unitPlans;
            this.startAddress = startAddress;
            this.endAddress = endAddress;
            this.path = path;
        }
    }

    @Getter
    public static class UpdateAllInOne {
        private Long dayPlanId;
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
