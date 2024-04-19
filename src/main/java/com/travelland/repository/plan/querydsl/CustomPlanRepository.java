package com.travelland.repository.plan.querydsl;


import com.travelland.dto.plan.PlanDto;

import java.util.List;

public interface CustomPlanRepository {

    List<PlanDto.GetList> getPlanList(Long lastId, int size, String sortBy, boolean isAsc);

    List<PlanDto.GetList> getPlanListByIds(List<Long> ids);
}
