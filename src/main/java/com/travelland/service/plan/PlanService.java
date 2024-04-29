package com.travelland.service.plan;

import com.travelland.domain.member.Member;
import com.travelland.domain.plan.*;
import com.travelland.dto.plan.*;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.global.security.UserDetailsImpl;
import com.travelland.repository.plan.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import static com.travelland.constant.Constants.PLAN_VIEW_COUNT;

@Service
@Transactional
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final DayPlanRepository dayPlanRepository;
    private final UnitPlanRepository unitPlanRepository;
    private final PlanVoteRepository planVoteRepository;
    private final PlanCommentRepository planCommentRepository;
    private final PlanLikeService planLikeService;
    private final PlanScrapService planScrapService;
    private final RedisTemplate<String,String> redisTemplate;

    // Plan 한방 작성: 3계층구조, Plan 1개 ⊃ DayPlan N개 ⊃ UnitPlan M개
    public PlanDto.Id createPlanAllInOne(PlanDto.CreateAllInOne request) {
        Member member = getLoginMember();
        Plan savedPlan = planRepository.save(new Plan(request, member));

        for (DayPlanDto.CreateAllInOne dayPlanDto : request.getDayPlans()) {
            DayPlan savedDayPlan = dayPlanRepository.save(new DayPlan(dayPlanDto, savedPlan));

            for (UnitPlanDto.CreateAllInOne unitPlanDto : dayPlanDto.getUnitPlans()) {
                UnitPlan savedUnitPlan = unitPlanRepository.save(new UnitPlan(unitPlanDto, savedDayPlan));
            }
        }

        return new PlanDto.Id(savedPlan);
    }

    // Plan 한방 단일상세 조회
    public PlanDto.GetAllInOne readPlanAllInOne(Long planId) {
        Plan plan = planRepository.findByIdAndIsDeleted(planId, false).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));

        // 비공개글인 경우, 로그인유저와 작성유저의 일치여부 검사
        if (plan.getIsPublic() == false) {
            checkAuth(getLoginMember().getId(), plan.getMember().getId(), ErrorCode.POST_GET_NOT_PERMISSION);
        }

        List<DayPlan> dayPlanList = dayPlanRepository.findAllByPlanIdAndIsDeleted(planId, false);
        List<DayPlanDto.GetAllInOne> dayPlanDtos = new ArrayList<>();

        // DayPlan에 UnitPlan 담는중
        for (DayPlan dayPlan : dayPlanList) {
            List<UnitPlan> unitPlanList = unitPlanRepository.findAllByDayPlanIdAndIsDeleted(dayPlan.getId(), false);
            if (unitPlanList == null)
                break;

            dayPlanDtos.add(DayPlanDto.GetAllInOne.builder()
                    .dayPlan(dayPlan)
                    .unitPlans(unitPlanList.stream().map(UnitPlanDto.GetAllInOne::new).toList())
                    .startAddress(unitPlanList.get(0).getAddress())
                    .endAddress(unitPlanList.get(unitPlanList.size()-1).getAddress())
                    .path(getPath(unitPlanList))
                    .build());
        }

        // Plan에 담을 PlanVote 준비중
        List<PlanVote> planVoteList = planVoteRepository.findAllByPlanAAndIsDeletedOrPlanBAndIsDeleted(plan, false, plan, false);
        planVoteList.forEach(PlanVote::checkTimeOut); // 투표기간이 종료됐는지 체크

        // Plan에 DayPlan과 PlanVote 담는중
        return PlanDto.GetAllInOne.builder()
                .plan(plan)
                .dayPlans(dayPlanDtos)
                .planVotes(planVoteList.stream().map(PlanVoteDto.GetAllInOne::new).toList())
                .isLike(planLikeService.statusPlanLike(planId))
                .isScrap(planScrapService.statusPlanScrap(planId))
                .isWriter(getIsWriterAndIncreaseViewCount(plan))
                .build();
    }

    // Plan 전체목록 조회: page는 1부터
    public Page<PlanDto.Get> readPlanList(int page, int size, String sortBy, boolean isAsc) {
        Page<Plan> plans = planRepository.findAllByIsDeletedAndIsPublic(getPageable(page, size, sortBy, isAsc), false, true);
        return plans.map(PlanDto.Get::new);
    }

    // (마이페이지용) Plan 유저별 전체목록 조회: page는 1부터
    public Page<PlanDto.Get> readPlanListForMember(int page, int size, String sortBy, boolean isAsc) {
        Member member = getLoginMember();
        Page<Plan> plans = planRepository.findAllByIsDeletedAndMemberId(getPageable(page, size, sortBy, isAsc), false, member.getId());
        return plans.map(PlanDto.Get::new);
    }

    // Plan 한방 수정
    public PlanDto.Id updatePlanAllInOne(Long planId, PlanDto.UpdateAllInOne request) {
        // Plan 수정
        Plan plan = planRepository.findByIdAndIsDeleted(planId, false).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));
        checkAuth(getLoginMember().getId(), plan.getMember().getId(), ErrorCode.POST_UPDATE_NOT_PERMISSION);
        plan.update(request);

        // DayPlan 수정
        for (DayPlanDto.UpdateAllInOne dayPlanDto : request.getDayPlans()) {
            DayPlan dayPlan = dayPlanRepository.findByIdAndIsDeleted(dayPlanDto.getDayPlanId(), false).orElseThrow(() -> new CustomException(ErrorCode.DAY_PLAN_NOT_FOUND));
            dayPlan.update(dayPlanDto);

            // UnitPlan 수정
            for (UnitPlanDto.UpdateAllInOne unitPlanDto : dayPlanDto.getUnitPlans()) {
                UnitPlan unitPlan = unitPlanRepository.findByIdAndIsDeleted(unitPlanDto.getUnitPlanId(), false).orElseThrow(() -> new CustomException(ErrorCode.UNIT_PLAN_NOT_FOUND));
                unitPlan.update(unitPlanDto);
            }
        }

        return new PlanDto.Id(plan);
    }

    // Plan 한방 삭제
    public PlanDto.Delete deletePlanAllInOne(Long planId) {
        Plan plan = planRepository.findByIdAndIsDeleted(planId, false).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));
        checkAuth(getLoginMember().getId(), plan.getMember().getId(), ErrorCode.POST_DELETE_NOT_PERMISSION);

        // 연관된 DayPlan 과 UnitPlan 을 먼저 삭제
        List<DayPlan> dayPlanList = dayPlanRepository.findAllByPlanIdAndIsDeleted(planId,false);
        for (DayPlan dayPlan : dayPlanList) {

            List<UnitPlan> unitPlanList = unitPlanRepository.findAllByDayPlanIdAndIsDeleted(dayPlan.getId(), false);
            for (UnitPlan unitPlan : unitPlanList) {
                unitPlan.delete();
            }

            dayPlan.delete();
        }

        // 연관된 PlanComment 먼저 삭제
        List<PlanComment> planCommentList = planCommentRepository.findAllByPlanIdAndIsDeleted(planId, false);
        for (PlanComment planComment : planCommentList) {
            planComment.delete();
        }

        // 마지막에서야 Plan 삭제
        plan.delete();
        return new PlanDto.Delete(plan.getIsDeleted());
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

    private void checkAuth(Long loginMemberId, Long writerId, ErrorCode errorCode) {
        if (loginMemberId != writerId) {
            throw new CustomException(errorCode);
        }
    }

    private Boolean getIsWriterAndIncreaseViewCount(Plan plan) {
        Boolean isWriter = false;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            Member member = ((UserDetailsImpl)authentication.getPrincipal()).getMember();

            // 로그인유저와 작성유저의 일치여부 검사, FE에서 수정삭제버튼 숨기기 위한 변수 isWriter 셋팅
            if(member.getId() == plan.getMember().getId()) {
                isWriter = true;
            }

            // 로그인유저가 안 본 글만 조회수 증가, Redis를 사용하여 이메일의 저장여부로 없는 경우에만 조회수 증가
            Long result = redisTemplate.opsForSet().add(PLAN_VIEW_COUNT + plan.getId(), member.getEmail());
            if (result != null && result == 1L) {
                plan.increaseViewCount();
            }
        } else { // 유저테스트 한정, 비로그인 시에도 조회수가 증가하도록 기획
            plan.increaseViewCount();
        }

        return isWriter;
    }

    private String getPath(List<UnitPlan> unitPlanList) {
        StringJoiner joiner = new StringJoiner(" >> ");
        for (UnitPlan unitPlan : unitPlanList) {
            joiner.add(unitPlan.getAddress());
        }
        return joiner.toString();
    }

    private Pageable getPageable(int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page-1, size, sort);
        return pageable;
    }
}
