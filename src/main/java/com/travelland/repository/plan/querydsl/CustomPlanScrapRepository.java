package com.travelland.repository.plan.querydsl;

import com.travelland.domain.member.Member;
import com.travelland.domain.plan.PlanScrap;
import com.travelland.dto.plan.PlanDto;
import com.travelland.dto.plan.PlanLikeScrapDto;

import java.util.List;

public interface CustomPlanScrapRepository {
    List<PlanLikeScrapDto.GetList> getScrapListByMember(Member member, int size, int page);
}

