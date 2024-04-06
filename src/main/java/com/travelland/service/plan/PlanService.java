package com.travelland.service.plan;

import com.travelland.domain.plan.DayPlan;
import com.travelland.domain.Member;
import com.travelland.domain.plan.Plan;
import com.travelland.domain.plan.UnitPlan;
import com.travelland.dto.DayPlanDto;
import com.travelland.dto.PlanDto;
import com.travelland.dto.UnitPlanDto;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.repository.plan.DayPlanRepository;
import com.travelland.repository.MemberRepository;
import com.travelland.repository.plan.PlanRepository;
import com.travelland.repository.plan.UnitPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional//(readOnly = true)
@RequiredArgsConstructor
public class PlanService {

    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;
    private final DayPlanRepository dayPlanRepository;
    private final UnitPlanRepository unitPlanRepository;

    private final StringRedisTemplate redisTemplate;
    private static final String PLAN_TOTAL_COUNT = "plan_total_count";

    // Plan 작성
    public PlanDto.Id createPlan(PlanDto.Create request, String email) {
//        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        Member member = userDetails.getMember();
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_MEMBER));

        Plan plan = new Plan(request, member);
        Plan savedPlan = planRepository.save(plan);

        //redisTemplate.opsForValue().increment(PLAN_TOTAL_COUNT);

        return new PlanDto.Id(savedPlan);
    }

    // Plan 전체조회
    public Page<PlanDto.Get> readPlanList(int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page-1, size, sort);

        Page<Plan> plans = planRepository.findAll(pageable);
        return plans.map(PlanDto.Get::new);
    }

    // Plan 전체조회 - Redis
    public List<PlanDto.GetList> readPlanListRedis(Long lastId, int size, String sortBy, boolean isASC) {
        return planRepository.getPlanList(lastId, size, sortBy, isASC);
    }

    // Plan 상세조회 (planId)
    public PlanDto.Get readPlanById(Long planId) {
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));
        return new PlanDto.Get(plan);
    }

    // Plan 수정
    public PlanDto.Id updatePlan(Long planId, PlanDto.Update request) {
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));
        Plan updatedPlan = plan.update(request);
        return new PlanDto.Id(updatedPlan);
    }

    // Plan 삭제
    public PlanDto.Delete deletePlan(Long planId) {
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));

        // 연관된 DayPlan과 UnitPlan을 먼저 삭제
        List<DayPlanDto.GetResponse> dayPlanList = readDayPlan(planId);
        for (DayPlanDto.GetResponse dayPlanDto : dayPlanList) {
            deleteDayPlan(dayPlanDto.getDayPlanId());
        }

        planRepository.delete(plan);
        return new PlanDto.Delete(true);
    }










    // DayPlan 작성
    public DayPlanDto.CreateResponse createDayPlan(Long planId, DayPlanDto.CreateRequest request) {
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));

        DayPlan dayPlan = new DayPlan(request, plan);
        DayPlan savedDayPlan = dayPlanRepository.save(dayPlan);
        return new DayPlanDto.CreateResponse(savedDayPlan);
    }

    // DayPlan 조회 (planId)
    public List<DayPlanDto.GetResponse> readDayPlan(Long planId) {
        List<DayPlan> dayPlanList = dayPlanRepository.findAllByPlanId(planId);

        if (dayPlanList.isEmpty()) {
            throw new CustomException(ErrorCode.DAY_PLAN_NOT_FOUND);
        }

        return dayPlanList.stream()
                .map(DayPlanDto.GetResponse::new)
                .toList();
    }

    // DayPlan 수정
    public DayPlanDto.UpdateResponse updateDayPlan(Long dayPlanId, DayPlanDto.UpdateRequest request) {
        DayPlan dayPlan = dayPlanRepository.findById(dayPlanId).orElseThrow(() -> new CustomException(ErrorCode.DAY_PLAN_NOT_FOUND));
        DayPlan updatedDayPlan = dayPlan.update(request);
        return new DayPlanDto.UpdateResponse(updatedDayPlan);
    }

    // DayPlan 삭제
    public DayPlanDto.DeleteResponse deleteDayPlan(Long dayPlanId) {
        DayPlan dayPlan = dayPlanRepository.findById(dayPlanId).orElseThrow(() -> new CustomException(ErrorCode.DAY_PLAN_NOT_FOUND));

        // 연관된 UnitPlan을 먼저 삭제
        List<UnitPlanDto.GetResponse> unitPlanList = readUnitPlan(dayPlanId);
        for (UnitPlanDto.GetResponse unitPlanDto : unitPlanList) {
            deleteUnitPlan(unitPlanDto.getUnitPlanId());
        }

        dayPlanRepository.delete(dayPlan);
        return new DayPlanDto.DeleteResponse(true);
    }










    // UnitPlan 작성
    public UnitPlanDto.CreateResponse createUnitPlan(Long dayPlanId, UnitPlanDto.CreateRequest request) {
        DayPlan dayPlan = dayPlanRepository.findById(dayPlanId).orElseThrow(() -> new CustomException(ErrorCode.DAY_PLAN_NOT_FOUND));

        UnitPlan unitPlan = new UnitPlan(request, dayPlan);
        UnitPlan savedUnitPlan = unitPlanRepository.save(unitPlan);
        return new UnitPlanDto.CreateResponse(savedUnitPlan);
    }

    // UnitPlan 조회 (dayPlanId)
    public List<UnitPlanDto.GetResponse> readUnitPlan(Long dayPlanId) {
        List<UnitPlan> unitPlanList = unitPlanRepository.findAllByDayPlanId(dayPlanId);

        if (unitPlanList.isEmpty()) {
            throw new CustomException(ErrorCode.UNIT_PLAN_NOT_FOUND);
        }

        return unitPlanList.stream()
                .map(UnitPlanDto.GetResponse::new)
                .toList();
    }

    // UnitPlan 수정
    public UnitPlanDto.UpdateResponse updateUnitPlan(Long unitPlanId, UnitPlanDto.UpdateRequest request) {
        UnitPlan unitPlan = unitPlanRepository.findById(unitPlanId).orElseThrow(() -> new CustomException(ErrorCode.UNIT_PLAN_NOT_FOUND));
        UnitPlan updatedUnitPlan = unitPlan.update(request);
        return new UnitPlanDto.UpdateResponse(updatedUnitPlan);
    }

    // UnitPlan 삭제
    public UnitPlanDto.DeleteResponse deleteUnitPlan(Long unitPlanId) {
        UnitPlan unitPlan = unitPlanRepository.findById(unitPlanId).orElseThrow(() -> new CustomException(ErrorCode.UNIT_PLAN_NOT_FOUND));
        unitPlanRepository.delete(unitPlan);
        return new UnitPlanDto.DeleteResponse(true);
    }
}
