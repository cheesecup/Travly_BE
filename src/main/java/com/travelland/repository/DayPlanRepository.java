package com.travelland.repository;

import com.travelland.domain.DayPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DayPlanRepository extends JpaRepository<DayPlan, Long> {

}
