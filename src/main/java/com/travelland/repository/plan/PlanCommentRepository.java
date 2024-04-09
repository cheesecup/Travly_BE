package com.travelland.repository.plan;

import com.travelland.domain.plan.PlanComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanCommentRepository extends JpaRepository<PlanComment, Long> {
    List<PlanComment> findAllByPlanIdAndIsDeleted(Long planId, boolean isDeleted);

    Page<PlanComment> findAllByPlanId(Pageable pageable, Long planId);
}
