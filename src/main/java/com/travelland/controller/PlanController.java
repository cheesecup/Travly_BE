package com.travelland.controller;

import com.travelland.controller.valid.PlanValidationSequence;
import com.travelland.global.security.UserDetailsImpl;
import com.travelland.swagger.PlanControllerDocs;
import com.travelland.dto.plan.DayPlanDto;
import com.travelland.dto.plan.PlanCommentDto;
import com.travelland.dto.plan.PlanDto;
import com.travelland.dto.plan.UnitPlanDto;
import com.travelland.service.plan.PlanLikeService;
import com.travelland.service.plan.PlanScrapService;
import com.travelland.service.plan.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class PlanController implements PlanControllerDocs {

    private final PlanService planService;
    private final PlanLikeService planLikeService;
    private final PlanScrapService planScrapService;

    // Plan 작성
    @PostMapping("/plans")
    public ResponseEntity<PlanDto.Id> createPlan(@Validated(PlanValidationSequence.class) @RequestBody PlanDto.Create request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(planService.createPlan(request));
    }

    // Plan 올인원한방 작성: Plan 안에 DayPlan N개, DayPlan 안에 UnitPlan M개, 3계층구조로 올인원 탑재
    @PostMapping("/plans/allInOn")
    public ResponseEntity<PlanDto.Id> createPlanAllInOne(@Validated(PlanValidationSequence.class) @RequestBody PlanDto.CreateAllInOne request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(planService.createPlanAllInOne(request));
    }

    // Plan 상세단일 조회
    @GetMapping("/plans/{planId}")
    public ResponseEntity<PlanDto.Get> readPlan(@PathVariable Long planId) {
        return ResponseEntity.status(HttpStatus.OK).body(planService.readPlan(planId));
    }

    // Plan 유저별 상세단일 조회
    @GetMapping("/users/plans/{planId}")
    public ResponseEntity<PlanDto.Get> readPlanForMember(@PathVariable Long planId) {
        return ResponseEntity.status(HttpStatus.OK).body(planService.readPlanForMember(planId));
    }

    // Plan 올인원한방 조회: Plan 안에 DayPlan N개, DayPlan 안에 UnitPlan M개, 3계층구조로 올인원 탑재
    @GetMapping("/plans/allInOn/{planId}")
    public ResponseEntity<PlanDto.GetAllInOne> readPlanAllInOne(@PathVariable Long planId) {
        return ResponseEntity.status(HttpStatus.OK).body(planService.readPlanAllInOne(planId));
    }

    // Plan 유저별 올인원한방 조회: Plan 안에 DayPlan N개, DayPlan 안에 UnitPlan M개, 3계층구조로 올인원 탑재
    @GetMapping("/users/plans/allInOn/{planId}")
    public ResponseEntity<PlanDto.GetAllInOne> readPlanAllInOneForMember(@PathVariable Long planId) {
        return ResponseEntity.status(HttpStatus.OK).body(planService.readPlanAllInOneForMember(planId));
    }

    // Plan 전체목록 조회
    @GetMapping("/plans") // 예시: /plans?page=1&size=20&sortBy=createdAt&isAsc=false, page 는 1부터
    public ResponseEntity<Page<PlanDto.Get>> readPlanList(@RequestParam int page,
                                                          @RequestParam int size,
                                                          @RequestParam String sortBy,
                                                          @RequestParam boolean isAsc) {
        return ResponseEntity.status(HttpStatus.OK).body(planService.readPlanList(page, size, sortBy, isAsc));
    }

    // Plan 유저별 전체목록 조회 (memberId)
    @GetMapping("/users/plans") // 예시: /plans?page=1&size=20&sortBy=createdAt&isAsc=false, page 는 1부터
    public ResponseEntity<Page<PlanDto.Get>> readPlanListForMember(@RequestParam int page,
                                                                   @RequestParam int size,
                                                                   @RequestParam String sortBy,
                                                                   @RequestParam boolean isAsc) {
        return ResponseEntity.status(HttpStatus.OK).body(planService.readPlanListForMember(page, size, sortBy, isAsc));
    }

    // Plan 전체조회 (Redis)
    @GetMapping("/plans/redis")
    public ResponseEntity<PlanDto.GetLists> readPlanListRedis(@RequestParam Long lastId,
                                                              @RequestParam int size,
                                                              @RequestParam String sortBy,
                                                              @RequestParam boolean isAsc) {
        return ResponseEntity.status(HttpStatus.OK).body(planService.readPlanListRedis(lastId, size, sortBy, isAsc));
    }

    // Plan 수정
    @PutMapping("/plans/{planId}")
    public ResponseEntity<PlanDto.Id> updatePlan(@PathVariable Long planId, @Validated(PlanValidationSequence.class) @RequestBody PlanDto.Update request) {
        return ResponseEntity.status(HttpStatus.OK).body(planService.updatePlan(planId, request));
    }

    // Plan 올인원한방 수정: Plan 안에 DayPlan N개, DayPlan 안에 UnitPlan M개, 3계층구조로 올인원 탑재
    @PutMapping("/plans/allInOn/{planId}")
    public ResponseEntity<PlanDto.Id> updatePlanAllInOne(@PathVariable Long planId, @Validated(PlanValidationSequence.class) @RequestBody PlanDto.UpdateAllInOne request) {
        return ResponseEntity.status(HttpStatus.OK).body(planService.updatePlanAllInOne(planId, request));
    }

    // Plan 삭제
    @DeleteMapping("/plans/{planId}")
    public ResponseEntity<PlanDto.Delete> deletePlan(@PathVariable Long planId) {
        return ResponseEntity.status(HttpStatus.OK).body(planService.deletePlanAllInOne(planId));
    }

    // Plan 올인원한방 삭제: Plan 안에 DayPlan N개, DayPlan 안에 UnitPlan M개, 3계층구조로 올인원 탑재
    @DeleteMapping("/plans/allInOn/{planId}")
    public ResponseEntity<PlanDto.Delete> deletePlanAllInOne(@PathVariable Long planId) {
        return ResponseEntity.status(HttpStatus.OK).body(planService.deletePlanAllInOne(planId));
    }










    // DayPlan 작성
    @PostMapping("/dayPlans/{planId}")
    public ResponseEntity<DayPlanDto.Id> createDayPlan(@PathVariable Long planId, @Validated(PlanValidationSequence.class) @RequestBody DayPlanDto.Create request) {
        DayPlanDto.Id response = planService.createDayPlan(planId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // DayPlan 조회 (planId)
    @GetMapping("/dayPlans/{planId}")
    public ResponseEntity<List<DayPlanDto.Get>> readDayPlan(@PathVariable Long planId) {
        List<DayPlanDto.Get> responses = planService.readDayPlan(planId);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    // DayPlan 수정
    @PutMapping("/dayPlans/{dayPlanId}")
    public ResponseEntity<DayPlanDto.Id> updateDayPlan(@PathVariable Long dayPlanId, @Validated(PlanValidationSequence.class) @RequestBody DayPlanDto.Update request) {
        DayPlanDto.Id response = planService.updateDayPlan(dayPlanId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // DayPlan 삭제
    @DeleteMapping("/dayPlans/{dayPlanId}")
    public ResponseEntity<DayPlanDto.Delete> deleteDayPlan(@PathVariable Long dayPlanId) {
        DayPlanDto.Delete response = planService.deleteDayPlan(dayPlanId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }










    // UnitPlan 작성
    @PostMapping("/unitPlans/{dayPlanId}")
    public ResponseEntity<UnitPlanDto.Id> createUnitPlan(@PathVariable Long dayPlanId, @Validated(PlanValidationSequence.class) @RequestBody UnitPlanDto.Create request) {
        UnitPlanDto.Id response = planService.createUnitPlan(dayPlanId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // UnitPlan 조회 (dayPlanId)
    @GetMapping("/unitPlans/{dayPlanId}")
    public ResponseEntity<List<UnitPlanDto.Get>> readUnitPlan(@PathVariable Long dayPlanId) {
        List<UnitPlanDto.Get> responses = planService.readUnitPlan(dayPlanId);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    // UnitPlan 수정
    @PutMapping("/unitPlans/{unitPlanId}")
    public ResponseEntity<UnitPlanDto.Id> updateUnitPlan(@PathVariable Long unitPlanId, @Validated(PlanValidationSequence.class) @RequestBody UnitPlanDto.Update request) {
        UnitPlanDto.Id response = planService.updateUnitPlan(unitPlanId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // UnitPlan 삭제
    @DeleteMapping("/unitPlans/{unitPlanId}")
    public ResponseEntity<UnitPlanDto.Delete> deleteUnitPlan(@PathVariable Long unitPlanId) {
        UnitPlanDto.Delete response = planService.deleteUnitPlan(unitPlanId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }










    // Plan 댓글 등록
    @PostMapping("/plans/{planId}/comments")
    public ResponseEntity<PlanCommentDto.Id> createPlanComment(@PathVariable Long planId, @Validated(PlanValidationSequence.class) @RequestBody PlanCommentDto.Create request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(planService.createPlanComment(planId, request));
    }

    // Plan 댓글 전체목록 조회 (planId)
    @GetMapping("/plans/{planId}/comments") // 예시: /plans/{planId}/comments?page=1&size=20&sortBy=createdAt&isAsc=false, page 는 1부터
    public ResponseEntity<Page<PlanCommentDto.Get>> readPlanCommentList(@PathVariable Long planId,
                                                                        @RequestParam int page,
                                                                        @RequestParam int size,
                                                                        @RequestParam String sortBy,
                                                                        @RequestParam boolean isAsc) {
        return ResponseEntity.status(HttpStatus.OK).body(planService.readPlanCommentList(planId, page, size, sortBy, isAsc));
    }

    // Plan 댓글 수정
    @PutMapping("/plans/{planId}/comments/{commentId}")
    public ResponseEntity<PlanCommentDto.Id> updatePlanComment(@PathVariable Long planId, @PathVariable Long commentId, @Validated(PlanValidationSequence.class) @RequestBody PlanCommentDto.Update request) {
        return ResponseEntity.status(HttpStatus.OK).body(planService.updatePlanComment(commentId, request));
    }

    // Plan 댓글 삭제
    @DeleteMapping("/plans/{planId}/comments/{commentId}")
    public ResponseEntity<PlanCommentDto.Delete> deletePlanComment(@PathVariable Long planId, @PathVariable Long commentId) {
        return ResponseEntity.status(HttpStatus.OK).body(planService.deletePlanComment(commentId));
    }









    // Plan 좋아요 등록
    @PostMapping("/plans/{planId}/like")
    public ResponseEntity<PlanDto.Result> createPlanLike(@PathVariable Long planId) {
        planLikeService.registerPlanLike(planId, "test@test.com");
        return ResponseEntity.status(HttpStatus.OK).body(new PlanDto.Result(true));
    }

    // Plan 좋아요 취소
    @DeleteMapping("/plans/{planId}/like")
    public ResponseEntity<PlanDto.Result> deletePlanLike(@PathVariable Long planId) {
        planLikeService.cancelPlanLike(planId, "test@test.com");
        return ResponseEntity.status(HttpStatus.OK).body(new PlanDto.Result(false));
    }

    // Plan 좋아요 전체목록 조회
    @GetMapping("/plans/like")
    public ResponseEntity<List<PlanDto.Likes>> getPlanLikeList(@RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(planLikeService.getPlanLikeList(page, size, "test@test.com"));
    }

    // Plan 스크랩 등록
    @PostMapping("/plans/{planId}/scrap")
    public ResponseEntity<PlanDto.Result> createPlanScrap(@PathVariable Long planId) {
        planScrapService.registerPlanScrap(planId, "test@test.com");
        return ResponseEntity.status(HttpStatus.OK).body(new PlanDto.Result(true));
    }

    // Plan 스크랩 취소
    @DeleteMapping("/plans/{planId}/scrap")
    public ResponseEntity<PlanDto.Result> deletePlanScrap(@PathVariable Long planId) {
        planScrapService.cancelPlanScrap(planId, "test@test.com");
        return ResponseEntity.status(HttpStatus.OK).body(new PlanDto.Result(false));
    }

    // Plan 스크랩 전체목록 조회
    @GetMapping("/plans/scrap")
    public ResponseEntity<List<PlanDto.Scraps>> getPlanScrapList(@RequestParam(defaultValue = "1") int page,
                                                                 @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(planScrapService.getPlanScrapList(page, size, "test@test.com"));
    }

    @PostMapping("/plans/{planId}/invite")
    public ResponseEntity<?> invitePlan(@PathVariable Long planId,
                                        @RequestBody PlanDto.Invitee invitee,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        planService.invitePlan(planId, invitee, userDetails.getMember().getNickname());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/plans/{planId}/invite/agree")
    public ResponseEntity<?> agreeInvitedPlan(@PathVariable Long planId,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        planService.agreeInvitedPlan(planId, userDetails.getMember().getNickname());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/plans/{planId}/invite/disagree")
    public ResponseEntity<?> disagreeInvitedPlan(@PathVariable Long planId,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        planService.disagreeInvitedPlan(planId, userDetails.getMember().getNickname());
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    // HTTPS 수신상태가 양호함을 AWS 와 통신하는 API
    @GetMapping("/healthcheck")
    public String healthcheck() {
        return "OK";
    }
}
