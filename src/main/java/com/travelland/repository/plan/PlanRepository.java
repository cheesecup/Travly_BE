package com.travelland.repository.plan;

import com.travelland.domain.plan.Plan;
import com.travelland.repository.plan.querydsl.CustomPlanRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long>, CustomPlanRepository {
    Optional<Plan> findByIdAndIsDeleted(Long planId, boolean isDeleted);

    Page<Plan> findAllByIsDeleted(Pageable pageable, boolean isDeleted);
}
