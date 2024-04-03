package com.travelland.repository.plan.querydsl;

import com.travelland.domain.Member;
import com.travelland.domain.plan.PlanLike;
import com.travelland.domain.plan.PlanScrap;

import java.util.List;

public interface CustomPlanScrapRepository {
    List<PlanScrap> getScrapListByMember(Member member, int size, int page);
}

