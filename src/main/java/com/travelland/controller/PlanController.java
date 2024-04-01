package com.travelland.controller;

import com.travelland.docs.PlanControllerDocs;
import com.travelland.dto.DayPlanDto;
import com.travelland.dto.PlanDto;
import com.travelland.dto.UnitPlanDto;
import com.travelland.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class PlanController implements PlanControllerDocs {

    private final PlanService planService;

    // Plan 작성
    @PostMapping("/plans")
    public ResponseEntity<PlanDto.CreateResponse> createPlan(@RequestBody PlanDto.CreateRequest request) {
        PlanDto.CreateResponse response = planService.createPlan(request, "a@email.com");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Plan 전체조회
    @GetMapping("/plans") // 예시: /plans?page=0&size=20&sortBy=createdAt&isAsc=false, page는 1부터
    public ResponseEntity<Page<PlanDto.ReadResponse>> readPlanList(
            @RequestParam int page, @RequestParam int size, @RequestParam String sortBy, @RequestParam boolean isAsc) {
        Page<PlanDto.ReadResponse> responses = planService.readPlanList(page, size, sortBy, isAsc);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    // Plan 상세조회 (planId)
    @GetMapping("/plans/{planId}")
    public ResponseEntity<PlanDto.ReadResponse> readPlanById(@PathVariable Long planId) {
        PlanDto.ReadResponse response = planService.readPlanById(planId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

//    // Plan 유저별조회 (memberId)
//    @GetMapping("/users/plans") // 예시: /users/plans?page=1&size=20&sort=등록일&ASC=false
//    public ResponseEntity<PlanDto.ReadResponse> getPlanListByMember(@RequestParam int page, @RequestParam int size, @RequestParam String sortBy, @RequestParam boolean isASC) {
//
//    }

    // Plan 수정
    @PutMapping("/plans/{planId}")
    public ResponseEntity<PlanDto.UpdateResponse> updatePlan(@PathVariable Long planId, @RequestBody PlanDto.UpdateRequest request) {
        PlanDto.UpdateResponse response = planService.updatePlan(planId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Plan 삭제
    @DeleteMapping("/plans/{planId}")
    public ResponseEntity<PlanDto.DeleteResponse> deletePlan(@PathVariable Long planId) {
        PlanDto.DeleteResponse response = planService.deletePlan(planId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }







    // DayPlan 작성
    @PostMapping("/plans/{planId}/dayplans")
    public ResponseEntity<DayPlanDto.CreateResponse> createDayPlan(@PathVariable Long planId, @RequestBody DayPlanDto.CreateRequest request) {
        DayPlanDto.CreateResponse response = planService.createDayPlan(planId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // DayPlan 상세조회 (dayPlanId)
    @GetMapping("/plans/{planId}/dayplans/{dayPlanId}")
    public ResponseEntity<DayPlanDto.ReadResponse> readDayPlan(@PathVariable Long planId, @PathVariable Long dayPlanId) {
        DayPlanDto.ReadResponse response = planService.readDayPlan(planId, dayPlanId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // DayPlan 수정
    @PutMapping("/plans/{planId}/dayplans/{dayPlanId}")
    public ResponseEntity<DayPlanDto.UpdateResponse> updateDayPlan(@PathVariable Long planId, @PathVariable Long dayPlanId, @RequestBody DayPlanDto.UpdateRequest request) {
        DayPlanDto.UpdateResponse response = planService.updateDayPlan(planId, dayPlanId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // DayPlan 삭제
    @DeleteMapping("/plans/{planId}/dayplans/{dayPlanId}")
    public ResponseEntity<DayPlanDto.DeleteResponse> deleteDayPlan(@PathVariable Long planId, @PathVariable Long dayPlanId) {
        DayPlanDto.DeleteResponse response = planService.deleteDayPlan(planId, dayPlanId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }







    // UnitPlan 작성
    @PostMapping("/plans/{planId}/dayplans/{dayPlanId}/unitplans")
    public ResponseEntity<UnitPlanDto.CreateResponse> createUnitPlan(@PathVariable Long planId, @PathVariable Long dayPlanId, @RequestBody UnitPlanDto.CreateRequest request) {
        UnitPlanDto.CreateResponse response = planService.createUnitPlan(planId, dayPlanId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // UnitPlan 상세조회 (dayPlanId)
    @GetMapping("/plans/{planId}/dayplans/{dayPlanId}/unitplans/{unitPlanId}")
    public ResponseEntity<UnitPlanDto.ReadResponse> readUnitPlan(@PathVariable Long planId, @PathVariable Long dayPlanId, @PathVariable Long unitPlanId) {
        UnitPlanDto.ReadResponse response = planService.readUnitPlan(planId, dayPlanId, unitPlanId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // UnitPlan 수정
    @PutMapping("/plans/{planId}/dayplans/{dayPlanId}/unitplans/{unitPlanId}")
    public ResponseEntity<UnitPlanDto.UpdateResponse> updateUnitPlan(@PathVariable Long planId, @PathVariable Long dayPlanId, @PathVariable Long unitPlanId, @RequestBody UnitPlanDto.UpdateRequest request) {
        UnitPlanDto.UpdateResponse response = planService.updateUnitPlan(planId, dayPlanId, unitPlanId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // UnitPlan 삭제
    @DeleteMapping("/plans/{planId}/dayplans/{dayPlanId}/unitplans/{unitPlanId}")
    public ResponseEntity<UnitPlanDto.DeleteResponse> deleteUnitPlan(@PathVariable Long planId, @PathVariable Long dayPlanId, @PathVariable Long unitPlanId) {
        UnitPlanDto.DeleteResponse response = planService.deleteUnitPlan(planId, dayPlanId, unitPlanId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    /*
    swagger
    @AuthenticationPrincipal UserDetailsImpl userDetails
     */
}
