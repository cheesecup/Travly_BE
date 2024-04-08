package com.travelland.repository.plan.querydsl;

import com.travelland.domain.member.Member;
import com.travelland.domain.plan.PlanLike;

import java.util.List;

public interface CustomPlanLikeRepository {
    List<PlanLike> getLikeListByMember(Member member, int size, int page);
}
