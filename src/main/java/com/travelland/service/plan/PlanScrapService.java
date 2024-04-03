package com.travelland.service.plan;

import com.travelland.domain.Member;
import com.travelland.domain.plan.Plan;
import com.travelland.domain.plan.PlanScrap;
import com.travelland.dto.PlanDto;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.repository.MemberRepository;
import com.travelland.repository.plan.PlanRepository;
import com.travelland.repository.plan.PlanScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanScrapService {

    private final PlanScrapRepository planScrapRepository;
    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;

    @Transactional
    public void registerPlanScrap(Long planId, String email) {
        Member member = getMember(email);
        Plan plan = getPlan(planId);

        planScrapRepository.findByMemberAndPlan(member, plan)
                .ifPresentOrElse(
                        PlanScrap::registerScrap,
                        () -> planScrapRepository.save(new PlanScrap(member, plan)));
    }

    @Transactional
    public void cancelPlanScrap(Long planId, String email) {
         planScrapRepository.findByMemberAndPlan(getMember(email), getPlan(planId))
                .orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND)).cancelScrap();
    }

    @Transactional(readOnly = true)
    public List<PlanDto.Scraps> getPlanScrapList(int page, int size, String email) {
        return planScrapRepository.getScrapListByMember(getMember(email),size, page)
                .stream().map(PlanDto.Scraps::new).toList();
    }

    @Transactional
    public void deleteTripScrap(Plan plan) {
        planScrapRepository.deleteAllByPlan(plan);
    }

    private Member getMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private Plan getPlan(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));
    }
}
