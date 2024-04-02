package com.travelland.repository;

import com.travelland.domain.UnitPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitPlanRepository extends JpaRepository<UnitPlan, Long> {
}
