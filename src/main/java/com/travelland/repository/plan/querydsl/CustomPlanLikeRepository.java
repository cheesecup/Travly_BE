package com.travelland.repository.plan.querydsl;

import com.travelland.domain.member.Member;
import com.travelland.domain.plan.PlanLike;
import com.travelland.dto.plan.PlanDto;
import com.travelland.dto.plan.PlanLikeScrapDto;

import java.util.List;

public interface CustomPlanLikeRepository {
    List<PlanLikeScrapDto.GetList> getLikeListByMember(Member member, int size, int page);
}
