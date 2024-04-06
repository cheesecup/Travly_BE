package com.travelland.controller;

import com.travelland.docs.PlanControllerDocs;
import com.travelland.dto.DayPlanDto;
import com.travelland.dto.PlanDto;
import com.travelland.dto.UnitPlanDto;
import com.travelland.service.plan.PlanLikeService;
import com.travelland.service.plan.PlanScrapService;
import com.travelland.service.plan.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    public ResponseEntity<PlanDto.Id> createPlan(@RequestBody PlanDto.Create request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body( planService.createPlan(request, "test@test.com"));
    }

    // Plan 전체조회
    @GetMapping("/plans") // 예시: /plans?page=0&size=20&sortBy=createdAt&isAsc=false, page는 1부터
    public ResponseEntity<Page<PlanDto.Get>> readPlanList(@RequestParam int page,
                                                          @RequestParam int size,
                                                          @RequestParam String sortBy,
                                                          @RequestParam boolean isAsc) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(planService.readPlanList(page, size, sortBy, isAsc));
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(planService.readPlanListRedis(lastId, size, sortBy, isAsc));

    }

    // Plan 전체조회 - Redis
    @GetMapping("/plans/redis") // 예시: /plans?page=0&size=20&sortBy=createdAt&isAsc=false, page는 1부터
    public ResponseEntity<List<PlanDto.GetList>> readPlanListRedis(@RequestParam Long lastId,
                                                               @RequestParam int size,
                                                               @RequestParam String sortBy,
                                                               @RequestParam boolean isAsc) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(planService.readPlanListRedis(lastId, size, sortBy, isAsc));
    }

    // Plan 상세조회 (planId)
    @GetMapping("/plans/{planId}")
    public ResponseEntity<PlanDto.Get> readPlanById(@PathVariable Long planId) {
        return ResponseEntity.status(HttpStatus.OK).body(planService.readPlanById(planId));
    }

    @GetMapping("/plans/allInOn/{planId}")
    public ResponseEntity<PlanDto.AllInOne> readPlanByIdOne(@PathVariable Long planId) {
        PlanDto.AllInOne allInOne = new PlanDto.AllInOne(planService.readPlanById(planId)) ;
        List<DayPlanDto.GetResponse> dayPlans = planService.readDayPlan(planId);
        List<DayPlanDto.AllInOne> ones = new ArrayList<>();

        for(DayPlanDto.GetResponse dayPlan : dayPlans){
            DayPlanDto.AllInOne newOne = new DayPlanDto.AllInOne(dayPlan);
            newOne.update(planService.readUnitPlan(dayPlan.getDayPlanId()));
            ones.add(newOne);
        }
        allInOne.updateDayPlan(ones);

        return ResponseEntity.status(HttpStatus.OK).body(allInOne);
    }

//    // Plan 유저별조회 (memberId)
//    @GetMapping("/users/plans") // 예시: /users/plans?page=1&size=20&sort=등록일&ASC=false
//    public ResponseEntity<PlanDto.ReadResponse> getPlanListByMember(@RequestParam int page, @RequestParam int size, @RequestParam String sortBy, @RequestParam boolean isASC) {
//
//    }

    // Plan 수정
    @PutMapping("/plans/{planId}")
    public ResponseEntity<PlanDto.Id> updatePlan(@PathVariable Long planId, @RequestBody PlanDto.Update request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(planService.updatePlan(planId, request));
    }

    // Plan 삭제
    @DeleteMapping("/plans/{planId}")
    public ResponseEntity<PlanDto.Delete> deletePlan(@PathVariable Long planId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(planService.deletePlan(planId));
    }










    //여행정보 좋아요 등록
    @PostMapping("/plans/{planId}/like")
    public ResponseEntity<PlanDto.Result> createPlanLike(@PathVariable Long planId) {
        planLikeService.registerPlanLike(planId, "test@test.com");
        return ResponseEntity.status(HttpStatus.OK).body(new PlanDto.Result(true));
    }

    //여행정보 좋아요 취소
    @DeleteMapping("/plans/{planId}/like")
    public ResponseEntity<PlanDto.Result> deletePlanLike(@PathVariable Long planId) {
        planLikeService.cancelPlanLike(planId, "test@test.com");
        return ResponseEntity.status(HttpStatus.OK).body(new PlanDto.Result(false));
    }

    //여행정보 좋아요 목록 조회
    @GetMapping("/plans/like")
    public ResponseEntity<List<PlanDto.Likes>> getPlanLikeList(@RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(planLikeService.getPlanLikeList(page, size, "test@test.com"));
    }

    //여행정보 스크랩 등록
    @PostMapping("/plans/{planId}/scrap")
    public ResponseEntity<PlanDto.Result> createPlanScrap(@PathVariable Long planId) {
        planScrapService.registerPlanScrap(planId, "test@test.com");
        return ResponseEntity.status(HttpStatus.OK).body(new PlanDto.Result(true));
    }

    //여행정보 스크랩 취소
    @DeleteMapping("/plans/{planId}/scrap")
    public ResponseEntity<PlanDto.Result> deletePlanScrap(@PathVariable Long planId) {
        planScrapService.cancelPlanScrap(planId, "test@test.com");
        return ResponseEntity.status(HttpStatus.OK).body(new PlanDto.Result(false));
    }

    //여행정보 스크랩 목록 조회
    @GetMapping("/plans/scrap")
    public ResponseEntity<List<PlanDto.Scraps>> getPlanScrapList(@RequestParam(defaultValue = "1") int page,
                                                                 @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(planScrapService.getPlanScrapList(page, size, "test@test.com"));
    }










    // DayPlan 작성
    @PostMapping("/dayPlans/{planId}")
    public ResponseEntity<DayPlanDto.CreateResponse> createDayPlan(@PathVariable Long planId, @RequestBody DayPlanDto.CreateRequest request) {
        DayPlanDto.CreateResponse response = planService.createDayPlan(planId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // DayPlan 조회 (planId)
    @GetMapping("/dayPlans/{planId}")
    public ResponseEntity<List<DayPlanDto.GetResponse>> readDayPlan(@PathVariable Long planId) {
        List<DayPlanDto.GetResponse> responses = planService.readDayPlan(planId);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    // DayPlan 수정
    @PutMapping("/dayPlans/{dayPlanId}")
    public ResponseEntity<DayPlanDto.UpdateResponse> updateDayPlan(@PathVariable Long dayPlanId, @RequestBody DayPlanDto.UpdateRequest request) {
        DayPlanDto.UpdateResponse response = planService.updateDayPlan(dayPlanId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // DayPlan 삭제
    @DeleteMapping("/dayPlans/{dayPlanId}")
    public ResponseEntity<DayPlanDto.DeleteResponse> deleteDayPlan(@PathVariable Long dayPlanId) {
        DayPlanDto.DeleteResponse response = planService.deleteDayPlan(dayPlanId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }










    // UnitPlan 작성
    @PostMapping("/unitPlans/{dayPlanId}")
    public ResponseEntity<UnitPlanDto.CreateResponse> createUnitPlan(@PathVariable Long dayPlanId, @RequestBody UnitPlanDto.CreateRequest request) {
        UnitPlanDto.CreateResponse response = planService.createUnitPlan(dayPlanId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // UnitPlan 조회 (dayPlanId)
    @GetMapping("/unitPlans/{dayPlanId}")
    public ResponseEntity<List<UnitPlanDto.GetResponse>> readUnitPlan(@PathVariable Long dayPlanId) {
        List<UnitPlanDto.GetResponse> responses = planService.readUnitPlan(dayPlanId);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    // UnitPlan 수정
    @PutMapping("/unitPlans/{unitPlanId}")
    public ResponseEntity<UnitPlanDto.UpdateResponse> updateUnitPlan(@PathVariable Long unitPlanId, @RequestBody UnitPlanDto.UpdateRequest request) {
        UnitPlanDto.UpdateResponse response = planService.updateUnitPlan(unitPlanId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // UnitPlan 삭제
    @DeleteMapping("/unitPlans/{unitPlanId}")
    public ResponseEntity<UnitPlanDto.DeleteResponse> deleteUnitPlan(@PathVariable Long unitPlanId) {
        UnitPlanDto.DeleteResponse response = planService.deleteUnitPlan(unitPlanId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }










    // HTTPS 수신상태가 양호함을 AWS와 통신하는 Controller
    @GetMapping("/healthcheck")
    public String healthcheck() {
        return "OK";
    }
}
