package com.travelland.repository.plan;

import com.travelland.domain.member.Member;
import com.travelland.domain.plan.Plan;
import com.travelland.domain.plan.PlanLike;
import com.travelland.dto.plan.PlanDto;
import com.travelland.repository.plan.querydsl.CustomPlanLikeRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlanLikeRepository extends JpaRepository<PlanLike, Long>, CustomPlanLikeRepository {
    Optional<PlanLike> findByMemberAndPlan(Member member, Plan plan);

    Optional<PlanLike> findByMemberAndPlanAndIsDeleted(Member member, Plan plan, boolean isDeleted);
}
