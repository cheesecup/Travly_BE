package com.travelland.repository.plan;

import com.travelland.domain.Member;
import com.travelland.domain.plan.Plan;
import com.travelland.domain.plan.PlanScrap;
import com.travelland.repository.plan.querydsl.CustomPlanScrapRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlanScrapRepository extends JpaRepository<PlanScrap, Long>, CustomPlanScrapRepository {

    void deleteAllByPlan(Plan plan);

    Optional<PlanScrap> findByMemberAndPlan(Member member, Plan plan);

    List<PlanScrap> getScrapListByMember(Member member, int size, int page);
}
