package com.travelland.service.plan;

import com.travelland.domain.member.Member;
import com.travelland.domain.plan.Plan;
import com.travelland.domain.plan.PlanScrap;
import com.travelland.dto.plan.PlanDto;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.global.security.UserDetailsImpl;
import com.travelland.repository.member.MemberRepository;
import com.travelland.repository.plan.PlanRepository;
import com.travelland.repository.plan.PlanScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanScrapService {

    private final PlanScrapRepository planScrapRepository;
    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;

    // Plan 스크랩 등록
    @Transactional
    public void registerPlanScrap(Long planId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = userDetails.getMember();
//        Member member = getMember("test@test.com");

        Plan plan = getPlan(planId);

        planScrapRepository.findByMemberAndPlan(member, plan)
                .ifPresentOrElse(
                        PlanScrap::registerScrap,
                        () -> planScrapRepository.save(new PlanScrap(member, plan)));
    }

    // Plan 스크랩 취소
    @Transactional
    public void cancelPlanScrap(Long planId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = userDetails.getMember();
//        Member member = getMember("test@test.com");

        planScrapRepository.findByMemberAndPlan(member, getPlan(planId))
                .orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND)).cancelScrap();
    }

    // Plan 스크랩 목록조회
    @Transactional(readOnly = true)
    public List<PlanDto.Scraps> getPlanScrapList(int page, int size) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = userDetails.getMember();
//        Member member = getMember("test@test.com");

        return planScrapRepository.getScrapListByMember(member,size, page)
                .stream().map(PlanDto.Scraps::new).toList();
    }

    // Plan 스크랩 데이터삭제
//    @Transactional
//    public void deletePlanScrap(Plan plan) {
//        planScrapRepository.deleteAllByPlan(plan);
//    }

    private Plan getPlan(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));
    }
}
