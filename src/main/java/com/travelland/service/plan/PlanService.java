package com.travelland.service.plan;

import com.travelland.domain.member.Member;
import com.travelland.domain.plan.DayPlan;
import com.travelland.domain.plan.Plan;
import com.travelland.domain.plan.PlanComment;
import com.travelland.domain.plan.UnitPlan;
import com.travelland.dto.plan.DayPlanDto;
import com.travelland.dto.plan.PlanCommentDto;
import com.travelland.dto.plan.PlanDto;
import com.travelland.dto.plan.UnitPlanDto;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.global.job.DataIntSet;
import com.travelland.global.job.DataStrSet;
import com.travelland.global.security.UserDetailsImpl;
import com.travelland.repository.member.MemberRepository;
import com.travelland.repository.plan.DayPlanRepository;
import com.travelland.repository.plan.PlanCommentRepository;
import com.travelland.repository.plan.PlanRepository;
import com.travelland.repository.plan.UnitPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.travelland.constant.Constants.PLAN_TOTAL_COUNT;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class PlanService {

    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;
    private final DayPlanRepository dayPlanRepository;
    private final UnitPlanRepository unitPlanRepository;
    private final PlanCommentRepository planCommentRepository;
    private final PlanLikeService planLikeService;
    private final PlanScrapService planScrapService;
    private final RedisTemplate<String,String> redisTemplate;


    // Plan 작성
    public PlanDto.Id createPlan(PlanDto.Create request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = userDetails.getMember();
//        Member member = memberRepository.findByEmail("test@test.com").orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_MEMBER));

        Plan plan = new Plan(request, member);
        Plan savedPlan = planRepository.save(plan);
        redisTemplate.opsForValue().increment(PLAN_TOTAL_COUNT);

        return new PlanDto.Id(savedPlan);
    }

    // Plan 올인원한방 작성: Plan 안에 DayPlan N개, DayPlan 안에 UnitPlan M개, 3계층구조로 올인원 탑재
    public PlanDto.Id createPlanAllInOne(PlanDto.CreateAllInOne request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = userDetails.getMember();
//        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_MEMBER));

        Plan plan = new Plan(request, member);
        Plan savedPlan = planRepository.save(plan);
        redisTemplate.opsForValue().increment(PLAN_TOTAL_COUNT);

        List<DayPlanDto.CreateAllInOne> dayPlanDtos = request.getDayPlans();
        for (DayPlanDto.CreateAllInOne dayPlanDto : dayPlanDtos) {
            DayPlan dayPlan = new DayPlan(dayPlanDto, plan);
            dayPlanRepository.save(dayPlan);

            List<UnitPlanDto.CreateAllInOne> unitPlanDtos = dayPlanDto.getUnitPlans();
            for (UnitPlanDto.CreateAllInOne unitPlanDto : unitPlanDtos) {
                UnitPlan unitPlan = new UnitPlan(unitPlanDto, dayPlan);
                unitPlanRepository.save(unitPlan);
            }
        }

        return new PlanDto.Id(savedPlan);
    }

    // Plan 상세단일 조회
    public PlanDto.Get readPlan(Long planId) {
        Plan plan = planRepository.findByIdAndIsDeletedAndIsPublic(planId, false, true).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));
        return new PlanDto.Get(plan);
    }

    // Plan 유저별 단일상세 조회
    public PlanDto.Get readPlanForMember(Long planId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = userDetails.getMember();
//        Member member = memberRepository.findByEmail("test@test.com").orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_MEMBER));

        Plan plan = planRepository.findByIdAndIsDeleted(planId, false).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));
        return new PlanDto.Get(plan);
    }

    // Plan 올인원한방 조회: Plan 안에 DayPlan N개, DayPlan 안에 UnitPlan M개, 3계층구조로 올인원 탑재
    public PlanDto.GetAllInOne readPlanAllInOne(Long planId) {
        List<DayPlan> dayPlanList = dayPlanRepository.findAllByPlanIdAndIsDeleted(planId, false);
        List<DayPlanDto.Get> dayPlanDtos = dayPlanList.stream().map(DayPlanDto.Get::new).toList();

        List<DayPlanDto.GetAllInOne> ones = new ArrayList<>();

        for (DayPlanDto.Get dayPlan : dayPlanDtos) {
            List<UnitPlan> unitPlanList = unitPlanRepository.findAllByDayPlanIdAndIsDeleted(dayPlan.getDayPlanId(), false);
            if (unitPlanList == null)
                break;
            List<UnitPlanDto.GetAllInOne> unitPlanDtos = unitPlanList.stream().map(UnitPlanDto.GetAllInOne::new).toList();

            // 첫번째/마지막 unitPlanList 의 address 를 가져옴
            String startAddress = unitPlanList.get(0).getAddress();
            String endAddress = unitPlanList.get(unitPlanList.size() - 1).getAddress();

            // Path 변수를 저장할 StringBuilder 생성
            StringBuilder path = new StringBuilder();
            boolean isFirst = true;

            // unitPlanList의 각 요소인 unitPlan의 address를 StringBuilder에 추가
            for (UnitPlan unitPlan : unitPlanList) {
                // 만약 현재 unitPlan이 마지막 요소가 아니라면 " >> "를 추가
                if (!isFirst) {
                    path.append(" >> ");
                }
                isFirst = false;
                path.append(unitPlan.getAddress());
            }

            // path 변수에 저장된 값을 가져옴
            String pathString = path.toString();

            ones.add(DayPlanDto.GetAllInOne.builder()
                    .dayPlan(dayPlan)
                    .unitPlans(unitPlanDtos)
                    .startAddress(startAddress)
                    .endAddress(endAddress)
                    .path(pathString)
                    .build());
        }

        return PlanDto.GetAllInOne.builder()
                .plan(readPlan(planId))
                .profileUrl("profileUrl")
                .dayPlans(ones).build();
    }

    // Plan 유저별 올인원한방 조회: Plan 안에 DayPlan N개, DayPlan 안에 UnitPlan M개, 3계층구조로 올인원 탑재
    public PlanDto.GetAllInOne readPlanAllInOneForMember(Long planId) {
        List<DayPlan> dayPlanList = dayPlanRepository.findAllByPlanIdAndIsDeleted(planId, false);
        List<DayPlanDto.Get> dayPlanDtos = dayPlanList.stream().map(DayPlanDto.Get::new).toList();

        List<DayPlanDto.GetAllInOne> ones = new ArrayList<>();

        for (DayPlanDto.Get dayPlan : dayPlanDtos) {
            List<UnitPlan> unitPlanList = unitPlanRepository.findAllByDayPlanIdAndIsDeleted(dayPlan.getDayPlanId(), false);
            if (unitPlanList == null)
                break;
            List<UnitPlanDto.GetAllInOne> unitPlanDtos = unitPlanList.stream().map(UnitPlanDto.GetAllInOne::new).toList();

            // 첫번째/마지막 unitPlanList 의 address 를 가져옴
            String startAddress = unitPlanList.get(0).getAddress();
            String endAddress = unitPlanList.get(unitPlanList.size() - 1).getAddress();

            // Path 변수를 저장할 StringBuilder 생성
            StringBuilder path = new StringBuilder();
            boolean isFirst = true;

            // unitPlanList의 각 요소인 unitPlan의 address를 StringBuilder에 추가
            for (UnitPlan unitPlan : unitPlanList) {
                // 만약 현재 unitPlan이 마지막 요소가 아니라면 " >> "를 추가
                if (!isFirst) {
                    path.append(" >> ");
                }
                isFirst = false;
                path.append(unitPlan.getAddress());
            }

            // path 변수에 저장된 값을 가져옴
            String pathString = path.toString();

            ones.add(DayPlanDto.GetAllInOne.builder()
                    .dayPlan(dayPlan)
                    .unitPlans(unitPlanDtos)
                    .startAddress(startAddress)
                    .endAddress(endAddress)
                    .path(pathString)
                    .build());
        }

        return PlanDto.GetAllInOne.builder()
                .plan(readPlanForMember(planId))
                .profileUrl("profileUrl")
                .dayPlans(ones).build();
    }

    // Plan 전체목록 조회
    public Page<PlanDto.Get> readPlanList(int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page-1, size, sort);

        Page<Plan> plans = planRepository.findAllByIsDeletedAndIsPublic(pageable, false, true);
        return plans.map(PlanDto.Get::new);
    }

    // Plan 유저별 전체목록 조회 (memberId)
    public Page<PlanDto.Get> readPlanListForMember(int page, int size, String sortBy, boolean isAsc) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = userDetails.getMember();
//        Member member = memberRepository.findByEmail("test@test.com").orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_MEMBER));

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page-1, size, sort);

        Page<Plan> plans = planRepository.findAllByMemberIdAndIsDeleted(pageable, false, member.getId());
        return plans.map(PlanDto.Get::new);
    }

    // Plan 전체목록 조회 (Redis)
    public PlanDto.GetLists readPlanListRedis(Long lastId, int size, String sortBy, boolean isASC) {
        List<PlanDto.GetList> list = planRepository.getPlanList(lastId, size, sortBy, isASC);
        return new PlanDto.GetLists(list, Long.parseLong(redisTemplate.opsForValue().get(PLAN_TOTAL_COUNT)));
    }

    // Plan 수정
    public PlanDto.Id updatePlan(Long planId, PlanDto.Update request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = userDetails.getMember();

        Plan plan = planRepository.findById(planId).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));

        if (member.getId() != plan.getMember().getId()) {
            throw new CustomException(ErrorCode.POST_UPDATE_NOT_PERMISSION);
        }

        Plan updatedPlan = plan.update(request);
        return new PlanDto.Id(updatedPlan);
    }

    // Plan 올인원한방 수정: Plan 안에 DayPlan N개, DayPlan 안에 UnitPlan M개, 3계층구조로 올인원 탑재
    public PlanDto.Id updatePlanAllInOne(Long planId, PlanDto.UpdateAllInOne request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = userDetails.getMember();

        Plan plan = planRepository.findById(planId).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));

        if (member.getId() != plan.getMember().getId()) {
            throw new CustomException(ErrorCode.POST_UPDATE_NOT_PERMISSION);
        }

        Plan updatedPlan = plan.update(request);

        List<DayPlanDto.UpdateAllInOne> dayPlanDtos = request.getDayPlans();
        for (DayPlanDto.UpdateAllInOne dayPlanDto : dayPlanDtos) {
            DayPlan dayPlan = dayPlanRepository.findById(dayPlanDto.getDayPlanId()).orElseThrow(() -> new CustomException(ErrorCode.DAY_PLAN_NOT_FOUND));
            dayPlan.update(dayPlanDto);

            List<UnitPlanDto.UpdateAllInOne> unitPlanDtos = dayPlanDto.getUnitPlans();
            for (UnitPlanDto.UpdateAllInOne unitPlanDto : unitPlanDtos) {
                UnitPlan unitPlan = unitPlanRepository.findById(unitPlanDto.getUnitPlanId()).orElseThrow(() -> new CustomException(ErrorCode.UNIT_PLAN_NOT_FOUND));
                unitPlan.update(unitPlanDto);
            }
        }

        return new PlanDto.Id(updatedPlan);
    }

    // Plan 올인원한방 삭제
    public PlanDto.Delete deletePlanAllInOne(Long planId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = userDetails.getMember();

        Plan plan = planRepository.findById(planId).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));
        System.out.println(plan.getId());
        System.out.println(plan.getMember().getId());
        System.out.println(member.getId());
        if (member.getId() != plan.getMember().getId()) {
            throw new CustomException(ErrorCode.POST_DELETE_NOT_PERMISSION);
        }

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
            deletePlanComment(planComment.getId());
        }

        plan.delete();
        return new PlanDto.Delete(plan.getIsDeleted());
    }

    @Transactional
    public void updateViewCount(DataIntSet dataIntSet){
        Plan plan = getPlan(dataIntSet.getId());
        plan.updateViewCount(dataIntSet.getValue());
        planRepository.save(plan);
    }

    public void syncPlanLike(List<DataStrSet> datas){
        datas.forEach(data -> planLikeService.savePlanLike(data.getId(), data.getValue()));
    }

    public void syncPlanScrap(List<DataStrSet> datas){
        datas.forEach(data -> planScrapService.savePlanScrap(data.getId(), data.getValue()));
    }









    // DayPlan 작성
    public DayPlanDto.Id createDayPlan(Long planId, DayPlanDto.Create request) {
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));

        DayPlan dayPlan = new DayPlan(request, plan);
        DayPlan savedDayPlan = dayPlanRepository.save(dayPlan);
        return new DayPlanDto.Id(savedDayPlan);
    }

    // DayPlan 조회 (planId)
    public List<DayPlanDto.Get> readDayPlan(Long planId) {
        List<DayPlan> dayPlanList = dayPlanRepository.findAllByPlanIdAndIsDeleted(planId, false);

        if (dayPlanList.isEmpty()) {
            throw new CustomException(ErrorCode.DAY_PLAN_NOT_FOUND);
        }

        return dayPlanList.stream()
                .map(DayPlanDto.Get::new)
                .toList();
    }

    // DayPlan 수정
    public DayPlanDto.Id updateDayPlan(Long dayPlanId, DayPlanDto.Update request) {
        DayPlan dayPlan = dayPlanRepository.findById(dayPlanId).orElseThrow(() -> new CustomException(ErrorCode.DAY_PLAN_NOT_FOUND));

        DayPlan updatedDayPlan = dayPlan.update(request);
        return new DayPlanDto.Id(updatedDayPlan);
    }

    // DayPlan 삭제
    public DayPlanDto.Delete deleteDayPlan(Long dayPlanId) {
        DayPlan dayPlan = dayPlanRepository.findById(dayPlanId).orElseThrow(() -> new CustomException(ErrorCode.DAY_PLAN_NOT_FOUND));

        // 연관된 UnitPlan 을 먼저 삭제
        List<UnitPlan> unitPlanList = unitPlanRepository.findAllByDayPlanIdAndIsDeleted(dayPlan.getId(), false);
        for (UnitPlan unitPlan : unitPlanList) {
            unitPlan.delete();
        }

        dayPlan.delete();
        return new DayPlanDto.Delete(dayPlan.getIsDeleted());
    }










    // UnitPlan 작성
    public UnitPlanDto.Id createUnitPlan(Long dayPlanId, UnitPlanDto.Create request) {
        DayPlan dayPlan = dayPlanRepository.findById(dayPlanId).orElseThrow(() -> new CustomException(ErrorCode.DAY_PLAN_NOT_FOUND));

        UnitPlan unitPlan = new UnitPlan(request, dayPlan);
        UnitPlan savedUnitPlan = unitPlanRepository.save(unitPlan);
        return new UnitPlanDto.Id(savedUnitPlan);
    }

    // UnitPlan 조회 (dayPlanId)
    public List<UnitPlanDto.Get> readUnitPlan(Long dayPlanId) {
        List<UnitPlan> unitPlanList = unitPlanRepository.findAllByDayPlanIdAndIsDeleted(dayPlanId, false);

        if (unitPlanList.isEmpty()) {
            throw new CustomException(ErrorCode.UNIT_PLAN_NOT_FOUND);
        }

        return unitPlanList.stream()
                .map(UnitPlanDto.Get::new)
                .toList();
    }

    // UnitPlan 수정
    public UnitPlanDto.Id updateUnitPlan(Long unitPlanId, UnitPlanDto.Update request) {
        UnitPlan unitPlan = unitPlanRepository.findById(unitPlanId).orElseThrow(() -> new CustomException(ErrorCode.UNIT_PLAN_NOT_FOUND));

        UnitPlan updatedUnitPlan = unitPlan.update(request);
        return new UnitPlanDto.Id(updatedUnitPlan);
    }

    // UnitPlan 삭제
    public UnitPlanDto.Delete deleteUnitPlan(Long unitPlanId) {
        UnitPlan unitPlan = unitPlanRepository.findById(unitPlanId).orElseThrow(() -> new CustomException(ErrorCode.UNIT_PLAN_NOT_FOUND));

        unitPlan.delete();
        return new UnitPlanDto.Delete(unitPlan.getIsDeleted());
    }










    // Plan 댓글 등록
    public PlanCommentDto.Id createPlanComment(Long planId, PlanCommentDto.Create request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = userDetails.getMember();
//        Member member = getMember("test@test.com");

        Plan plan = getPlan(planId);

        PlanComment planComment = new PlanComment(request, member, plan);
        PlanComment savedPlanComment = planCommentRepository.save(planComment);

        return new PlanCommentDto.Id(savedPlanComment);
    }

    // Plan 댓글 전체목록 조회 (planId)
    public Page<PlanCommentDto.Get> readPlanCommentList(Long planId, int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page-1, size, sort);

        Page<PlanComment> planComments = planCommentRepository.findAllByPlanId(pageable, planId);
        return planComments.map(PlanCommentDto.Get::new);
    }

    // Plan 댓글 수정
    public PlanCommentDto.Id updatePlanComment(Long commentId, PlanCommentDto.Update request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = userDetails.getMember();

        PlanComment planComment = planCommentRepository.findById(commentId).orElseThrow(() -> new CustomException(ErrorCode.PLAN_COMMENT_NOT_FOUND));

        if (member.getId() != planComment.getMember().getId()) {
            throw new CustomException(ErrorCode.POST_UPDATE_NOT_PERMISSION);
        }

        PlanComment updatedPlanComment = planComment.update(request);
        return new PlanCommentDto.Id(updatedPlanComment);
    }

    // Plan 댓글 삭제
    public PlanCommentDto.Delete deletePlanComment(Long commentId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = userDetails.getMember();

        PlanComment planComment = planCommentRepository.findById(commentId).orElseThrow(() -> new CustomException(ErrorCode.PLAN_COMMENT_NOT_FOUND));

        if (member.getId() != planComment.getMember().getId()) {
            throw new CustomException(ErrorCode.POST_DELETE_NOT_PERMISSION);
        }

        planComment.delete();
        return new PlanCommentDto.Delete(planComment.getIsDeleted());
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
