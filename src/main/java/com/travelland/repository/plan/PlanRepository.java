package com.travelland.repository.plan;

import com.travelland.domain.plan.Plan;
import com.travelland.repository.plan.querydsl.CustomPlanRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long>, CustomPlanRepository {

//    List<Plan> getPlanList(int page, int size, String sort, boolean ASC);

}
