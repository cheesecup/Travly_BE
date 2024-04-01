package com.travelland.docs;

import com.travelland.dto.DayPlanDto;
import com.travelland.dto.PlanDto;
import com.travelland.dto.UnitPlanDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "PlanController", description = "Plan, DayPlan, UnitPlan 모두 포함")
public interface PlanControllerDocs {

    @Operation(summary = "Plan 작성", description = " ")
    ResponseEntity<PlanDto.CreateResponse> createPlan(@RequestBody PlanDto.CreateRequest request);

    @Operation(summary = "Plan 전체조회", description = "전체조회, /plans?page=0&size=20&sortBy=createdAt&isAsc=false, page는 1부터")
    ResponseEntity<Page<PlanDto.ReadResponse>> readPlanList(
            @RequestParam int page, @RequestParam int size, @RequestParam String sortBy, @RequestParam boolean isAsc);

    @Operation(summary = "Plan 상세조회", description = "상세조회, planId로 조회, userId는 로그인구현 후")
    ResponseEntity<PlanDto.ReadResponse> readPlanById(@PathVariable Long planId);

    @Operation(summary = "Plan 수정", description = " ")
    ResponseEntity<PlanDto.UpdateResponse> updatePlan(@PathVariable Long planId, @RequestBody PlanDto.UpdateRequest request);

    @Operation(summary = "Plan 삭제", description = " ")
    ResponseEntity<PlanDto.DeleteResponse> deletePlan(@PathVariable Long planId);







    @Operation(summary = "DayPlan 작성", description = " ")
    ResponseEntity<DayPlanDto.CreateResponse> createDayPlan(@PathVariable Long planId, @RequestBody DayPlanDto.CreateRequest request);

    @Operation(summary = "DayPlan 상세조회", description = " ")
    ResponseEntity<DayPlanDto.ReadResponse> readDayPlan(@PathVariable Long planId, @PathVariable Long dayPlanId);

    @Operation(summary = "DayPlan 수정", description = " ")
    ResponseEntity<DayPlanDto.UpdateResponse> updateDayPlan(@PathVariable Long planId, @PathVariable Long dayPlanId, @RequestBody DayPlanDto.UpdateRequest request);

    @Operation(summary = "DayPlan 삭제", description = " ")
    ResponseEntity<DayPlanDto.DeleteResponse> deleteDayPlan(@PathVariable Long planId, @PathVariable Long dayPlanId);







    @Operation(summary = "UnitPlan 작성", description = " ")
    ResponseEntity<UnitPlanDto.CreateResponse> createUnitPlan(@PathVariable Long planId, @PathVariable Long dayPlanId, @RequestBody UnitPlanDto.CreateRequest request);

    @Operation(summary = "UnitPlan 상세조회", description = " ")
    ResponseEntity<UnitPlanDto.ReadResponse> readUnitPlan(@PathVariable Long planId, @PathVariable Long dayPlanId, @PathVariable Long unitPlanId);

    @Operation(summary = "UnitPlan 수정", description = " ")
    ResponseEntity<UnitPlanDto.UpdateResponse> updateUnitPlan(@PathVariable Long planId, @PathVariable Long dayPlanId, @PathVariable Long unitPlanId, @RequestBody UnitPlanDto.UpdateRequest request);

    @Operation(summary = "UnitPlan 삭제", description = " ")
    ResponseEntity<UnitPlanDto.DeleteResponse> deleteUnitPlan(@PathVariable Long planId, @PathVariable Long dayPlanId, @PathVariable Long unitPlanId);
}