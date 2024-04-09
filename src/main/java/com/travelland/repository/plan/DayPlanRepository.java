package com.travelland.repository.plan;

import com.travelland.domain.plan.DayPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DayPlanRepository extends JpaRepository<DayPlan, Long> {
    List<DayPlan> findAllByPlanIdAndIsDeleted(Long planId, boolean isDeleted);
}
