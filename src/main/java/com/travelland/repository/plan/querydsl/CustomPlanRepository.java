package com.travelland.repository.plan.querydsl;


import com.travelland.domain.plan.Plan;
import com.travelland.dto.plan.PlanDto;

import java.util.List;

public interface CustomPlanRepository {
    List<PlanDto.GetList> getPlanList(Long lastId, int size, String sortBy, boolean isAsc);

    Plan readPlanAllInOneQuery(Long planId);
}
