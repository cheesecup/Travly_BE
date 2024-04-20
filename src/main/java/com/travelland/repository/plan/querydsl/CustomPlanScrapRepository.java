package com.travelland.repository.plan.querydsl;

import com.travelland.domain.member.Member;
import com.travelland.domain.plan.PlanScrap;
import com.travelland.dto.plan.PlanDto;

import java.util.List;

public interface CustomPlanScrapRepository {
    List<PlanDto.GetList> getScrapListByMember(Member member, int size, int page);
}

