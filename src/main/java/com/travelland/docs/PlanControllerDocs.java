package com.travelland.docs;

import com.travelland.dto.DayPlanDto;
import com.travelland.dto.PlanDto;
import com.travelland.dto.UnitPlanDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "PlanController", description = "Plan, DayPlan, UnitPlan 모두 포함")
public interface PlanControllerDocs {

    @Operation(summary = "Plan 작성", description = " ")
    ResponseEntity createPlan(@RequestBody PlanDto.CreateRequest request);

    @Operation(summary = "Plan 전체조회", description = "전체조회, /plans?page=0&size=20&sortBy=createdAt&isAsc=false, page는 1부터")
    ResponseEntity readPlanList(
            @RequestParam int page, @RequestParam int size, @RequestParam String sortBy, @RequestParam boolean isAsc);

    @Operation(summary = "Plan 상세조회", description = "상세조회, planId로 조회, userId는 로그인구현 후")
    ResponseEntity readPlanById(@PathVariable Long planId);

    @Operation(summary = "Plan 수정", description = " ")
    ResponseEntity updatePlan(@PathVariable Long planId, @RequestBody PlanDto.UpdateRequest request);

    @Operation(summary = "Plan 삭제", description = " ")
    ResponseEntity deletePlan(@PathVariable Long planId);







    @Operation(summary = "DayPlan 작성", description = " ")
    ResponseEntity createDayPlan(@PathVariable Long planId, @RequestBody DayPlanDto.CreateRequest request);

    @Operation(summary = "DayPlan 상세조회", description = " ")
    ResponseEntity readDayPlan(@PathVariable Long planId, @PathVariable Long dayPlanId);

    @Operation(summary = "DayPlan 수정", description = " ")
    ResponseEntity updateDayPlan(@PathVariable Long planId, @PathVariable Long dayPlanId, @RequestBody DayPlanDto.UpdateRequest request);

    @Operation(summary = "DayPlan 삭제", description = " ")
    ResponseEntity deleteDayPlan(@PathVariable Long planId, @PathVariable Long dayPlanId);







    @Operation(summary = "UnitPlan 작성", description = " ")
    ResponseEntity createUnitPlan(@PathVariable Long planId, @PathVariable Long dayPlanId, @RequestBody UnitPlanDto.CreateRequest request);

    @Operation(summary = "UnitPlan 상세조회", description = " ")
    ResponseEntity readUnitPlan(@PathVariable Long planId, @PathVariable Long dayPlanId, @PathVariable Long unitPlanId);

    @Operation(summary = "UnitPlan 수정", description = " ")
    ResponseEntity updateUnitPlan(@PathVariable Long planId, @PathVariable Long dayPlanId, @PathVariable Long unitPlanId, @RequestBody UnitPlanDto.UpdateRequest request);

    @Operation(summary = "UnitPlan 삭제", description = " ")
    ResponseEntity deleteUnitPlan(@PathVariable Long planId, @PathVariable Long dayPlanId, @PathVariable Long unitPlanId);
}