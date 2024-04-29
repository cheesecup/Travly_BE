package com.travelland.service.plan;

import com.travelland.domain.plan.DayPlan;
import com.travelland.domain.plan.Plan;
import com.travelland.domain.plan.UnitPlan;
import com.travelland.dto.plan.PlanToTripDto;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.repository.plan.DayPlanRepository;
import com.travelland.repository.plan.PlanRepository;
import com.travelland.repository.plan.UnitPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PlanToTripService {

    private final PlanRepository planRepository;
    private final DayPlanRepository dayPlanRepository;
    private final UnitPlanRepository unitPlanRepository;

    // (여행후기용) Plan->Trip 데이터 변환
    public PlanToTripDto transferPlanToTrip(Long planId) {
        StringBuilder contentBuilder = new StringBuilder();
        Plan plan = planRepository.findByIdAndIsDeletedAndIsPublic(planId, false, true).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));

        List<DayPlan> dayPlanList = dayPlanRepository.findAllByPlanIdAndIsDeleted(planId, false);
        for (DayPlan dayPlan : dayPlanList) {
            contentBuilder.append("날짜:").append(dayPlan.getDate()).append("\n");

            List<UnitPlan> unitPlanList = unitPlanRepository.findAllByDayPlanIdAndIsDeleted(dayPlan.getId(), false);
            for (UnitPlan unitPlan : unitPlanList) {
                contentBuilder.append("시간:").append(unitPlan.getTime()).append("\n");
                contentBuilder.append("장소:").append((unitPlan.getAddress()+" "+unitPlan.getPlaceName()).replaceAll("null", "")).append("\n");
                contentBuilder.append("예산:").append(unitPlan.getBudget()).append("원\n");
                contentBuilder.append("제목:").append(unitPlan.getTitle()).append("\n");
                contentBuilder.append("내용:").append(unitPlan.getContent()).append("\n\n");
            }
        }

        String content = contentBuilder.toString();
        return new PlanToTripDto(plan, content);
    }
}
