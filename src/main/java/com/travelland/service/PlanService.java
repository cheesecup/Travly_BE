package com.travelland.service;

import com.travelland.domain.DayPlan;
import com.travelland.domain.Member;
import com.travelland.domain.Plan;
import com.travelland.domain.UnitPlan;
import com.travelland.dto.DayPlanDto;
import com.travelland.dto.PlanDto;
import com.travelland.dto.UnitPlanDto;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.repository.DayPlanRepository;
import com.travelland.repository.MemberRepository;
import com.travelland.repository.PlanRepository;
import com.travelland.repository.UnitPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional//(readOnly = true)
@RequiredArgsConstructor
public class PlanService {

    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;
    private final DayPlanRepository dayPlanRepository;
    private final UnitPlanRepository unitPlanRepository;

    // Plan 작성
    public PlanDto.CreateResponse createPlan(PlanDto.CreateRequest request, String email) {
//        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        Member member = userDetails.getMember();
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_MEMBER));

        Plan plan = new Plan(request, member);
        Plan savedPlan = planRepository.save(plan);
        return new PlanDto.CreateResponse(savedPlan);
    }

    // Plan 전체조회
    public Page<PlanDto.ReadResponse> readPlanList(int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page-1, size, sort);

        Page<Plan> plans = planRepository.findAll(pageable);
        return plans.map(PlanDto.ReadResponse::new);
    }

    // Plan 상세조회 (planId)
    public PlanDto.ReadResponse readPlanById(Long planId) {
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));
        return new PlanDto.ReadResponse(plan);
    }

    // Plan 수정
    public PlanDto.UpdateResponse updatePlan(Long planId, PlanDto.UpdateRequest request) {
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));
        Plan updatedPlan = plan.update(request);
        return new PlanDto.UpdateResponse(updatedPlan);
    }

    // Plan 삭제
    public PlanDto.DeleteResponse deletePlan(Long planId) {
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));
        planRepository.delete(plan);
        return new PlanDto.DeleteResponse(true);
    }







    // DayPlan 작성
    public DayPlanDto.CreateResponse createDayPlan(Long planId, DayPlanDto.CreateRequest request) {
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));

        DayPlan dayPlan = new DayPlan(request, plan);
        DayPlan savedDayPlan = dayPlanRepository.save(dayPlan);
        return new DayPlanDto.CreateResponse(savedDayPlan);
    }

    // DayPlan 상세조회 (dayPlanId)
    public DayPlanDto.ReadResponse readDayPlan(Long planId, Long dayPlanId) {
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));

        DayPlan dayPlan = dayPlanRepository.findById(dayPlanId).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));
        return new DayPlanDto.ReadResponse(dayPlan);
    }

    // DayPlan 수정
    public DayPlanDto.UpdateResponse updateDayPlan(Long planId, Long dayPlanId, DayPlanDto.UpdateRequest request) {
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));

        DayPlan dayPlan = dayPlanRepository.findById(dayPlanId).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));
        DayPlan updatedDayPlan = dayPlan.update(request);
        return new DayPlanDto.UpdateResponse(updatedDayPlan);
    }

    // DayPlan 삭제
    public DayPlanDto.DeleteResponse deleteDayPlan(Long planId, Long dayPlanId) {
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));

        DayPlan dayPlan = dayPlanRepository.findById(dayPlanId).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));
        dayPlanRepository.delete(dayPlan);
        return new DayPlanDto.DeleteResponse(true);
    }







    // UnitPlan 작성
    public UnitPlanDto.CreateResponse createUnitPlan(Long planId, Long dayPlanId, UnitPlanDto.CreateRequest request) {
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));
        DayPlan dayPlan = dayPlanRepository.findById(dayPlanId).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));

        UnitPlan unitPlan = new UnitPlan(request, dayPlan);
        UnitPlan savedUnitPlan = unitPlanRepository.save(unitPlan);
        return new UnitPlanDto.CreateResponse(savedUnitPlan);
    }

    // UnitPlan 상세조회 (unitPlanId)
    public UnitPlanDto.ReadResponse readUnitPlan(Long planId, Long dayPlanId, Long unitPlanId) {
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));
        DayPlan dayPlan = dayPlanRepository.findById(dayPlanId).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));

        UnitPlan unitPlan = unitPlanRepository.findById(unitPlanId).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));
        return new UnitPlanDto.ReadResponse(unitPlan);
    }

    // UnitPlan 수정
    public UnitPlanDto.UpdateResponse updateUnitPlan(Long planId, Long dayPlanId, Long unitPlanId, UnitPlanDto.UpdateRequest request) {
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));
        DayPlan dayPlan = dayPlanRepository.findById(dayPlanId).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));

        UnitPlan unitPlan = unitPlanRepository.findById(unitPlanId).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));
        UnitPlan updatedUnitPlan = unitPlan.update(request);
        return new UnitPlanDto.UpdateResponse(updatedUnitPlan);
    }

    // UnitPlan 삭제
    public UnitPlanDto.DeleteResponse deleteUnitPlan(Long planId, Long dayPlanId, Long unitPlanId) {
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));
        DayPlan dayPlan = dayPlanRepository.findById(dayPlanId).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));

        UnitPlan unitPlan = unitPlanRepository.findById(unitPlanId).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));
        unitPlanRepository.delete(unitPlan);
        return new UnitPlanDto.DeleteResponse(true);
    }
}
