package com.travelland.service.plan;

import com.travelland.domain.member.Member;
import com.travelland.domain.plan.Plan;
import com.travelland.domain.plan.PlanLike;
import com.travelland.domain.trip.Trip;
import com.travelland.domain.trip.TripLike;
import com.travelland.dto.plan.PlanDto;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.repository.member.MemberRepository;
import com.travelland.repository.plan.PlanLikeRepository;
import com.travelland.repository.plan.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.travelland.constant.Constants.PLAN_LIKES_EMAIL;
import static com.travelland.constant.Constants.PLAN_LIKES_PLAN_ID;

@Service
@RequiredArgsConstructor
public class PlanLikeService {

    private final PlanRepository planRepository;
    private final PlanLikeRepository planLikeRepository;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String,String> redisTemplate;

    public void registerPlanLike(Long planId, String email) {
        redisTemplate.opsForSet().add(PLAN_LIKES_PLAN_ID + planId, email);
        redisTemplate.opsForList().rightPush(PLAN_LIKES_EMAIL + email, planId.toString());
    }

    public void cancelPlanLike(Long planId, String email) {
        redisTemplate.opsForSet().remove(PLAN_LIKES_PLAN_ID + planId, email);
        redisTemplate.opsForList().remove(PLAN_LIKES_EMAIL + email,0, planId);
    }
    @Transactional
    public void savePlanLike(Long planId, String email) {
        Member member = getMember(email);
        Plan plan = getTrip(planId);

        planLikeRepository.findByMemberAndPlan(member, plan)
                .ifPresentOrElse(
                        PlanLike::registerLike, // 좋아요를 한번이라도 등록한적이 있을경우
                        () -> planLikeRepository.save(new PlanLike(member, plan)) // 최초로 좋아요를 등록하는 경우
                );
    }


    public List<PlanDto.GetList> getPlanLikeList(int page, int size, String email) {
        List<String> planIds = redisTemplate.opsForList()
                .range(PLAN_LIKES_EMAIL + email, (long) (page - 1) * size, (long) page * size - 1);

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
