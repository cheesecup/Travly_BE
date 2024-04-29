package com.travelland.service.plan;

import com.travelland.domain.member.Member;
import com.travelland.domain.plan.Plan;
import com.travelland.domain.plan.PlanLike;
import com.travelland.dto.plan.PlanLikeScrapDto;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.global.security.UserDetailsImpl;
import com.travelland.repository.member.MemberRepository;
import com.travelland.repository.plan.PlanLikeRepository;
import com.travelland.repository.plan.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.travelland.constant.Constants.PLAN_LIKES_PLAN_ID;

@Service
@RequiredArgsConstructor
public class PlanLikeService {

    private final PlanRepository planRepository;
    private final PlanLikeRepository planLikeRepository;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String,String> redisTemplate;

    // Plan 좋아요 등록
    @Transactional
    public void registerPlanLike(Long planId) {
        Member member = getLoginMember();
        String email = member.getEmail();
        Plan plan = getPlan(planId);

        plan.increaseLikeCount(); // 좋아요수 증가 (스크랩은 스크립수 증가 없음)
        planLikeRepository.findByMemberAndPlan(member, plan)
                .ifPresentOrElse(
                        PlanLike::registerLike, // 좋아요를 한번이라도 등록한적이 있을경우
                        () -> planLikeRepository.save(new PlanLike(member, plan)) // 최초로 좋아요를 등록하는 경우
                );
        redisTemplate.opsForSet().add(PLAN_LIKES_PLAN_ID + planId, email);
    }

    // Plan 좋아요 취소
    @Transactional
    public void cancelPlanLike(Long planId) {
        String email = getLoginMember().getEmail();
        Plan plan = getPlan(planId);

        plan.decreaseLikeCount(); // 좋아요수 감소 (스크랩은 스크립수 감소 없음)
        getPlanLike(planId, email).cancelLike();
        redisTemplate.opsForSet().remove(PLAN_LIKES_PLAN_ID + planId, email);
    }

    // Plan 좋아요 유저별 전체목록 조회
    @Transactional(readOnly = true)
    public List<PlanLikeScrapDto.GetList> getPlanLikeList(int page, int size) {
        return  planLikeRepository.getLikeListByMember(getLoginMember(), size, page);
    }

    // Plan 좋아요 여부 확인
    public boolean statusPlanLike(Long planId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            Member member = getLoginMember();
            String email = member.getEmail();

            // Redis 에서 확인
            if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(PLAN_LIKES_PLAN_ID + planId, email)))
                return true;

            // Redis 에 없는 경우, DB에서 한번 더 확인
            Optional<PlanLike> planLike = planLikeRepository.findByMemberAndPlanAndIsDeleted(getMember(email), getPlan(planId), false);
            if (planLike.isPresent()) {
                redisTemplate.opsForSet().add(PLAN_LIKES_PLAN_ID + planId, email);
                return true;
            }
        }

        return false;
    }










    private Member getLoginMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            Member member = ((UserDetailsImpl)authentication.getPrincipal()).getMember();
            return member;
        } else {
            throw new CustomException(ErrorCode.STATUS_NOT_LOGIN);
        }
    }

    private Member getMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private Plan getPlan(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));
    }

    private PlanLike getPlanLike(Long planId, String email) {
        return planLikeRepository.findByMemberAndPlan(getMember(email), getPlan(planId))
                .orElseThrow(()-> new CustomException(ErrorCode.POST_LIKE_NOT_FOUND));
    }
}
