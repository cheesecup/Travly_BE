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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.travelland.constant.Constants.PLAN_SCRAPS_PLAN_ID;

@Service
@RequiredArgsConstructor
public class PlanScrapService {

    private final PlanRepository planRepository;
    private final PlanScrapRepository planScrapRepository;
    private final MemberRepository memberRepository;
    private final  RedisTemplate<String, String> redisTemplate;

    @Transactional
    public void registerPlanScrap(Long planId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            Member member = ((UserDetailsImpl)authentication.getPrincipal()).getMember();
            String email = member.getEmail();

            Plan plan = getPlan(planId);
            planScrapRepository.findByMemberAndPlan(member, plan)
                    .ifPresentOrElse(
                            PlanScrap::registerScrap, // 스크랩을 한번이라도 등록한적이 있을경우
                            () -> planScrapRepository.save(new PlanScrap(member, plan)) // 최초로 좋아요를 등록하는 경우
                    );
            redisTemplate.opsForSet().add(PLAN_SCRAPS_PLAN_ID + planId, email);
        }
    }

    // Plan 스크랩 취소
    @Transactional
    public void cancelPlanScrap(Long planId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            Member member = ((UserDetailsImpl)authentication.getPrincipal()).getMember();
            String email = member.getEmail();

            getPlanScrap(planId,email).cancelScrap();
            redisTemplate.opsForSet().remove(PLAN_SCRAPS_PLAN_ID + planId, email);
        }
    }

    // Plan 스크랩 유저별 전체목록 조회
    @Transactional(readOnly = true)
    public List<PlanDto.GetList> getPlanScrapList(int page, int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = ((UserDetailsImpl)authentication.getPrincipal()).getMember();
        String email = member.getEmail();

        return planScrapRepository.getScrapListByMember(getMember(email),size,page);
    }

    // Plan 스크랩 여부 확인
    public boolean statusPlanScrap(Long planId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            Member member = ((UserDetailsImpl) authentication.getPrincipal()).getMember();
            String email = member.getEmail();

            if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(PLAN_SCRAPS_PLAN_ID + planId, email)))
                return true;
            Optional<PlanScrap> planScrap = planScrapRepository.findByMemberAndPlanAndIsDeleted(getMember(email), getPlan(planId), false);
            if (planScrap.isPresent()) {
                redisTemplate.opsForSet().add(PLAN_SCRAPS_PLAN_ID + planId, email);
                return true;
            }
        }
        return false;
    }

    private PlanScrap getPlanScrap(Long planId, String email) {
        return planScrapRepository.findByMemberAndPlan(getMember(email), getPlan(planId))
                .orElseThrow(()-> new CustomException(ErrorCode.POST_LIKE_NOT_FOUND));
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
