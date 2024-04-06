package com.travelland.repository.plan.querydsl;


import com.travelland.dto.PlanDto;

import java.util.List;

public interface CustomPlanRepository {

    List<PlanDto.GetList> getPlanList(Long lastId, int size, String sortBy, boolean isAsc);

}
