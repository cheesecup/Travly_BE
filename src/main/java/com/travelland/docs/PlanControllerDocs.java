package com.travelland.docs;

import com.travelland.dto.DayPlanDto;
import com.travelland.dto.PlanCommentDto;
import com.travelland.dto.PlanDto;
import com.travelland.dto.UnitPlanDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "여행 플랜 API", description = "Plan, DayPlan, UnitPlan 모두 포함")
public interface PlanControllerDocs {

    @Operation(summary = "Plan 작성", description = " ")
    ResponseEntity createPlan(@RequestBody PlanDto.Create request);

    @Operation(summary = "Plan 한방 작성", description = " ")
    ResponseEntity createPlanAllInOne(@RequestBody PlanDto.CreateAllInOne request);

    @Operation(summary = "Plan 상세/단일 조회", description = "상세조회, planId로 조회, userId는 로그인구현 후")
    ResponseEntity readPlanById(@PathVariable Long planId);

    @Operation(summary = "Plan 한방 상세/단일 조회", description = "한방 상세조회, planId로 조회, userId는 로그인구현 후")
    ResponseEntity readPlanAllInOne(@PathVariable Long planId);

    @Operation(summary = "Plan 전체목록 조회", description = "전체조회, /plans?page=0&size=20&sortBy=createdAt&isAsc=false, page는 1부터")
    ResponseEntity readPlanList(@RequestParam int page, @RequestParam int size, @RequestParam String sortBy, @RequestParam boolean isAsc);

    @Operation(summary = "Plan 전체목록 조회", description = " ")
    ResponseEntity readPlanListRedis(@RequestParam Long lastId, @RequestParam int size, @RequestParam String sortBy, @RequestParam boolean isAsc);

    @Operation(summary = "Plan 수정", description = " ")
    ResponseEntity updatePlan(@PathVariable Long planId, @RequestBody PlanDto.Update request);

    @Operation(summary = "Plan 한방 수정", description = " ")
    ResponseEntity updatePlanAllInOne(@PathVariable Long planId, @RequestBody PlanDto.UpdateAllInOne request);

    @Operation(summary = "Plan 한방 삭제 (구 API 주소)", description = "API 주소만 예전주소일뿐 동작은 똑같이 가능")
    ResponseEntity deletePlan(@PathVariable Long planId);

    @Operation(summary = "Plan 한방 삭제", description = " ")
    ResponseEntity deletePlanAllInOne(@PathVariable Long planId);










    @Operation(summary = "DayPlan 작성", description = " ")
    ResponseEntity createDayPlan(@PathVariable Long planId, @RequestBody DayPlanDto.Create request);

    @Operation(summary = "DayPlan 조회", description = "planId로 조회")
    ResponseEntity readDayPlan(@PathVariable Long planId);

    @Operation(summary = "DayPlan 수정", description = " ")
    ResponseEntity updateDayPlan(@PathVariable Long dayPlanId, @RequestBody DayPlanDto.Update request);

    @Operation(summary = "DayPlan 삭제", description = " ")
    ResponseEntity deleteDayPlan(@PathVariable Long dayPlanId);










    @Operation(summary = "UnitPlan 작성", description = " ")
    ResponseEntity createUnitPlan(@PathVariable Long dayPlanId, @RequestBody UnitPlanDto.Create request);

    @Operation(summary = "UnitPlan 조회", description = "dayPlanId로 조회")
    ResponseEntity readUnitPlan(@PathVariable Long dayPlanId);

    @Operation(summary = "UnitPlan 수정", description = " ")
    ResponseEntity updateUnitPlan(@PathVariable Long unitPlanId, @RequestBody UnitPlanDto.Update request);

    @Operation(summary = "UnitPlan 삭제", description = " ")
    ResponseEntity deleteUnitPlan(@PathVariable Long unitPlanId);










    @Operation(summary = "Plan 댓글 등록", description = " ")
    ResponseEntity createPlanComment(@PathVariable Long planId, @RequestBody PlanCommentDto.Create request);

    @Operation(summary = "Plan 댓글 조회", description = " ")
    ResponseEntity readPlanCommentList(@PathVariable Long planId, @RequestParam int page, @RequestParam int size, @RequestParam String sortBy, @RequestParam boolean isAsc);

    @Operation(summary = "Plan 댓글 수정", description = " ")
    ResponseEntity updatePlanComment(@PathVariable Long planId, @PathVariable Long commentId, @RequestBody PlanCommentDto.Update request);

    @Operation(summary = "Plan 댓글 삭제", description = " ")
    ResponseEntity deletePlanComment(@PathVariable Long planId, @PathVariable Long commentId);










    @Operation(summary = "Plan 좋아요 등록", description = "선택한 Plan 좋아요를 등록하는 API")
    ResponseEntity<PlanDto.Result> createPlanLike(@PathVariable Long planId) ;

    @Operation(summary = "Plan 좋아요 취소", description = "선택한 Plan 좋아요를 취소하는 API")
    ResponseEntity<PlanDto.Result> deletePlanLike(@PathVariable Long planId);

    @Operation(summary = "Plan 좋아요 목록조회", description = "좋아요을 누른 Plan 목록을 페이지별로 조회하는 API")
    ResponseEntity<List<PlanDto.Likes>> getPlanLikeList(@RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Plan 스크랩 등록", description = "선택한 Plan 스크랩에 추가하는 API")
    ResponseEntity<PlanDto.Result> createPlanScrap(@PathVariable Long planId);

    @Operation(summary = "Plan 스크랩 취소", description = "선택한 Plan 스크랩에서 삭제하는 API")
    ResponseEntity<PlanDto.Result> deletePlanScrap(@PathVariable Long planId);

    @Operation(summary = "Plan 스크랩 목록조회", description = "스크랩한 Plan 목록을 페이지별로 조회하는 API")
    ResponseEntity<List<PlanDto.Scraps>> getPlanScrapList(@RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "20") int size);










    @Operation(summary = "(백엔드용) HTTPS 기능", description = "HTTPS 수신상태가 양호함을 AWS 와 통신하는 API")
    String healthcheck();
}