package com.travelland.service.plan;

import com.travelland.domain.Member;
import com.travelland.domain.plan.Plan;
import com.travelland.domain.plan.PlanLike;
import com.travelland.dto.PlanDto;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.repository.member.MemberRepository;
import com.travelland.repository.plan.PlanLikeRepository;
import com.travelland.repository.plan.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanLikeService {

    private final PlanLikeRepository planLikeRepository;
    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;

    //여행정보 좋아요 등록
    @Transactional
    public void registerPlanLike(Long planId, String email) {
        Member member = getMember(email);
        Plan plan = getPlan(planId);

        planLikeRepository.findByMemberAndPlan(member, plan)
                .ifPresentOrElse(
                        PlanLike::registerLike, // 좋아요를 한번이라도 등록한적이 있을경우
                        () -> {
                            PlanLike planLike = new PlanLike(member, plan);
                            planLikeRepository.save(planLike); // 최초로 좋아요를 등록하는 경우
                            plan.increaseLikeCount(); // 좋아요 수 증가
                        }
                );
    }

    //여행정보 좋아요 취소
    @Transactional
    public void cancelPlanLike(Long planId, String email) {
        Plan plan = getPlan(planId);

        planLikeRepository.findByMemberAndPlan(getMember(email), plan)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_LIKE_NOT_FOUND))
                .cancelLike();

        plan.decreaseLikeCount();
    }

    //여행정보 좋아요 목록 조회
    @Transactional(readOnly = true)
    public List<PlanDto.Likes> getPlanLikeList(int page, int size, String email) {
        return planLikeRepository
                .getLikeListByMember(getMember(email), size, page)
                .stream().map(PlanDto.Likes::new).toList();
    }
    
    //좋아요 데이터 삭제
    @Transactional
    public void deletePlanLike(Plan plan) {
        planLikeRepository.deleteAllByPlan(plan);
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
