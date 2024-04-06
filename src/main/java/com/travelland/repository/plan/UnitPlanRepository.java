package com.travelland.repository.plan;

import com.travelland.domain.plan.UnitPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitPlanRepository extends JpaRepository<UnitPlan, Long> {
    List<UnitPlan> findAllByDayPlanId(Long dayPlanId);
}
