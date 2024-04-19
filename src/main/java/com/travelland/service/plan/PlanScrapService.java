package com.travelland.service.plan;

import com.travelland.domain.member.Member;
import com.travelland.domain.plan.Plan;
import com.travelland.domain.plan.PlanLike;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.travelland.constant.Constants.*;

@Service
@RequiredArgsConstructor
public class PlanScrapService {

    private final PlanRepository planRepository;
    private final PlanScrapRepository planScrapRepository;
    private final MemberRepository memberRepository;
    private final  RedisTemplate<String, String> redisTemplate;

    public void registerPlanScrap(Long planId, String email) {
        redisTemplate.opsForSet().add(PLAN_SCRAPS_TRIP_ID + planId, email);
        redisTemplate.opsForList().rightPush(PLAN_SCRAPS_EMAIL + email, planId.toString());
    }

    public void cancelPlanScrap(Long planId, String email) {
        redisTemplate.opsForSet().remove(PLAN_SCRAPS_TRIP_ID + planId, email);
        redisTemplate.opsForList().remove(PLAN_SCRAPS_EMAIL + email, 0, planId);
    }

    @Transactional
    public void savePlanScrap(Long planId, String email) {
        Member member = getMember(email);
        Plan plan = getTrip(planId);

        planScrapRepository.findByMemberAndPlan(member, plan)
                .ifPresentOrElse(
                        PlanScrap::registerScrap, // 좋아요를 한번이라도 등록한적이 있을경우
                        () -> planScrapRepository.save(new PlanScrap(member, plan)) // 최초로 좋아요를 등록하는 경우
                );
    }

    public List<PlanDto.GetList> getPlanScrapList(int page, int size, String email) {
        List<String> planIds = redisTemplate.opsForList()
                .range(PLAN_SCRAPS_EMAIL + email, (long) (page - 1) * size, (long) page * size - 1);

        if(planIds == null)
            return new ArrayList<>();

        return planRepository.getPlanListByIds(planIds.stream().map(Long::parseLong).toList());
    }
    private Member getMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private Plan getTrip(Long tripId) {
        return planRepository.findById(tripId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }
}
