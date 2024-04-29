package com.travelland.repository.plan.querydsl;


import com.travelland.domain.plan.Plan;
import com.travelland.dto.plan.PlanDto;
import com.travelland.dto.plan.PlanLikeScrapDto;

import java.util.List;

public interface CustomPlanRepository {
    List<PlanLikeScrapDto.GetList> getPlanList(Long lastId, int size, String sortBy, boolean isAsc);

    List<PlanLikeScrapDto.GetList> getPlanListByIds(List<Long> ids);
    Plan readPlanAllInOneQuery(Long planId);
}
