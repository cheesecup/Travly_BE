package com.travelland.repository.plan;

import com.travelland.domain.member.Member;
import com.travelland.domain.plan.Plan;
import com.travelland.domain.plan.PlanScrap;
import com.travelland.repository.plan.querydsl.CustomPlanScrapRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlanScrapRepository extends JpaRepository<PlanScrap, Long>, CustomPlanScrapRepository {
    Optional<PlanScrap> findByMemberAndPlan(Member member, Plan plan);

    Optional<PlanScrap> findByMemberAndPlanAndIsDeleted(Member member, Plan plan, boolean b);
}
