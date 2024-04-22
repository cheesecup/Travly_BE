package com.travelland.repository.plan;

import com.travelland.domain.plan.PlanComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlanCommentRepository extends JpaRepository<PlanComment, Long> {
    List<PlanComment> findAllByPlanIdAndIsDeleted(Long planId, boolean isDeleted);

    Page<PlanComment> findAllByPlanIdAndIsDeleted(Pageable pageable, Long planId, boolean isDeleted);

    Optional<PlanComment> findByIdAndIsDeleted(Long commentId, boolean isDeleted);
}
