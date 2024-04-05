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

import java.util.List;

@Tag(name = "여행 플랜 API", description = "Plan, DayPlan, UnitPlan 모두 포함")
public interface PlanControllerDocs {

    @Operation(summary = "Plan 작성", description = " ")
    ResponseEntity createPlan(@RequestBody PlanDto.Create request);

    @Operation(summary = "Plan 전체목록조회", description = "전체조회, /plans?page=0&size=20&sortBy=createdAt&isAsc=false, page는 1부터")
    ResponseEntity readPlanList(@RequestParam int page, @RequestParam int size, @RequestParam String sortBy, @RequestParam boolean isAsc);

    @Operation(summary = "Plan 전체목록조회", description = "전체조회, /plans?page=0&size=20&sortBy=createdAt&isAsc=false, page는 1부터")
    ResponseEntity readPlanListRedis(@RequestParam int page, @RequestParam int size, @RequestParam String sortBy, @RequestParam boolean isAsc);

    @Operation(summary = "Plan 상세조회", description = "상세조회, planId로 조회, userId는 로그인구현 후")
    ResponseEntity readPlanById(@PathVariable Long planId);

    @Operation(summary = "Plan 수정", description = " ")
    ResponseEntity updatePlan(@PathVariable Long planId, @RequestBody PlanDto.Update request);

    @Operation(summary = "Plan 삭제", description = " ")
    ResponseEntity deletePlan(@PathVariable Long planId);










    @Operation(summary = "Plan 좋아요 등록", description = "선택한 Plan 좋아요를 등록하는 API")
    ResponseEntity<PlanDto.Result> createPlanLike(@PathVariable Long planId) ;

    @Operation(summary = "Plan 좋아요 취소", description = "선택한 Plan 좋아요를 취소하는 API")
    ResponseEntity<PlanDto.Result> deletePlanLike(@PathVariable Long planId);

    @Operation(summary = "Plan 좋아요 목록 조회", description = "좋아요을 누른 Plan 목록을 페이지별로 조회하는 API")
    ResponseEntity<List<PlanDto.Likes>> getPlanLikeList(@RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Plan 스크랩 추가", description = "선택한 Plan 스크랩에 추가하는 API")
    ResponseEntity<PlanDto.Result> createPlanScrap(@PathVariable Long planId);

    @Operation(summary = "Plan 스크랩 취소", description = "선택한 Plan 스크랩에서 삭제하는 API")
    ResponseEntity<PlanDto.Result> deletePlanScrap(@PathVariable Long planId);

    @Operation(summary = "Plan 스크랩 목록 조회", description = "스크랩한 Plan 목록을 페이지별로 조회하는 API")
    ResponseEntity<List<PlanDto.Scraps>> getPlanScrapList(@RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "20") int size);










    @Operation(summary = "DayPlan 작성", description = " ")
    ResponseEntity createDayPlan(@PathVariable Long planId, @RequestBody DayPlanDto.CreateRequest request);

    @Operation(summary = "DayPlan 조회", description = "planId로 조회")
    ResponseEntity readDayPlan(@PathVariable Long planId);

    @Operation(summary = "DayPlan 수정", description = " ")
    ResponseEntity updateDayPlan(@PathVariable Long dayPlanId, @RequestBody DayPlanDto.UpdateRequest request);

    @Operation(summary = "DayPlan 삭제", description = " ")
    ResponseEntity deleteDayPlan(@PathVariable Long dayPlanId);










    @Operation(summary = "UnitPlan 작성", description = " ")
    ResponseEntity createUnitPlan(@PathVariable Long dayPlanId, @RequestBody UnitPlanDto.CreateRequest request);

    @Operation(summary = "UnitPlan 조회", description = "dayPlanId로 조회")
    ResponseEntity readUnitPlan(@PathVariable Long dayPlanId);

    @Operation(summary = "UnitPlan 수정", description = " ")
    ResponseEntity updateUnitPlan(@PathVariable Long unitPlanId, @RequestBody UnitPlanDto.UpdateRequest request);

    @Operation(summary = "UnitPlan 삭제", description = " ")
    ResponseEntity deleteUnitPlan(@PathVariable Long unitPlanId);
}