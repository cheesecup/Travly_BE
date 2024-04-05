package com.travelland.repository.plan.querydsl;


import com.travelland.domain.plan.Plan;

import java.util.List;

public interface CustomPlanRepository {

    List<Plan> getPlanList(int page, int size, String sort, boolean ASC);

}
