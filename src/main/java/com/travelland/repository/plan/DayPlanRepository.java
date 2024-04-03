package com.travelland.repository.plan;

import com.travelland.domain.plan.DayPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DayPlanRepository extends JpaRepository<DayPlan, Long> {

}
