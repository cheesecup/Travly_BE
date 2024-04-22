package com.travelland.swagger;

import com.travelland.global.security.UserDetailsImpl;
import com.travelland.valid.plan.PlanValidationSequence;
import com.travelland.dto.plan.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "여행 플랜 API", description = "여행 전 플랜 관련 API")
public interface PlanControllerDocs {

//    @Operation(summary = "Plan 작성", description = " ")
//    ResponseEntity createPlan(@RequestBody PlanDto.Create request);

    @Operation(summary = "Plan 한방 작성", description = "Plan 안에 DayPlan N개, DayPlan 안에 UnitPlan M개, 3계층구조로 올인원 탑재")
    ResponseEntity createPlanAllInOne(@RequestBody PlanDto.CreateAllInOne request);

//    @Operation(summary = "Plan 상세/단일 조회", description = " ")
//    ResponseEntity readPlan(@PathVariable Long planId);
//
//    @Operation(summary = "(마이페이지용) Plan 유저별 상세/단일 조회", description = " ")
//    ResponseEntity readPlanForMember(@PathVariable Long planId);

    @Operation(summary = "Plan 한방 상세/단일 조회", description = "Plan 안에 DayPlan N개, DayPlan 안에 UnitPlan M개, 3계층구조로 올인원 탑재")
    ResponseEntity readPlanAllInOne(@PathVariable Long planId);

    @Operation(summary = "(마이페이지용) Plan 유저별 한방 상세/단일 조회", description = "Plan 안에 DayPlan N개, DayPlan 안에 UnitPlan M개, 3계층구조로 올인원 탑재")
    ResponseEntity readPlanAllInOneForMember(@PathVariable Long planId);

    @Operation(summary = "Plan 전체목록 조회", description = "page는 1부터")
    ResponseEntity readPlanList(@RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "20") int size,
                                @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                @RequestParam(required = false, defaultValue = "false") boolean isAsc);

    @Operation(summary = "(마이페이지용) Plan 유저별 전체목록 조회", description = "page는 1부터")
    ResponseEntity readPlanListForMember(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "20") int size,
                                         @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                         @RequestParam(required = false, defaultValue = "false") boolean isAsc);

//    @Operation(summary = "Plan 전체목록 조회", description = " ")
//    ResponseEntity readPlanListRedis(@RequestParam Long lastId, @RequestParam int size, @RequestParam String sortBy, @RequestParam boolean isAsc);
//
//    @Operation(summary = "Plan 수정", description = " ")
//    ResponseEntity updatePlan(@PathVariable Long planId, @RequestBody PlanDto.Update request);

    @Operation(summary = "Plan 한방 수정", description = "Plan 안에 DayPlan N개, DayPlan 안에 UnitPlan M개, 3계층구조로 올인원 탑재")
    ResponseEntity updatePlanAllInOne(@PathVariable Long planId, @RequestBody PlanDto.UpdateAllInOne request);

    @Operation(summary = "Plan 한방 삭제", description = "Plan 안에 DayPlan N개, DayPlan 안에 UnitPlan M개, 3계층구조로 올인원 탑재")
    ResponseEntity deletePlanAllInOne(@PathVariable Long planId);










//    @Operation(summary = "DayPlan 작성", description = " ")
//    ResponseEntity createDayPlan(@PathVariable Long planId, @RequestBody DayPlanDto.Create request);
//
//    @Operation(summary = "DayPlan 조회", description = "planId로 조회")
//    ResponseEntity readDayPlan(@PathVariable Long planId);
//
//    @Operation(summary = "DayPlan 수정", description = " ")
//    ResponseEntity updateDayPlan(@PathVariable Long dayPlanId, @RequestBody DayPlanDto.Update request);
//
//    @Operation(summary = "DayPlan 삭제", description = " ")
//    ResponseEntity deleteDayPlan(@PathVariable Long dayPlanId);










//    @Operation(summary = "UnitPlan 작성", description = " ")
//    ResponseEntity createUnitPlan(@PathVariable Long dayPlanId, @RequestBody UnitPlanDto.Create request);
//
//    @Operation(summary = "UnitPlan 조회", description = "dayPlanId로 조회")
//    ResponseEntity readUnitPlan(@PathVariable Long dayPlanId);
//
//    @Operation(summary = "UnitPlan 수정", description = " ")
//    ResponseEntity updateUnitPlan(@PathVariable Long unitPlanId, @RequestBody UnitPlanDto.Update request);
//
//    @Operation(summary = "UnitPlan 삭제", description = " ")
//    ResponseEntity deleteUnitPlan(@PathVariable Long unitPlanId);










    @Operation(summary = "PlanVote(투표장) 생성", description = "투표기간(Duration): { 1분(ONE_MINUTE), 1초(ONE_SECOND), 12시간(HALF_DAY), 1일(ONE_DAY), 3일(THREE_DAY), 7일(SEVEN_DAY) }")
    ResponseEntity createPlanVote(@Validated(PlanValidationSequence.class) @RequestBody PlanVoteDto.Create request);

    @Operation(summary = "PlanVote(투표장) 상세/단일 조회", description = " ")
    ResponseEntity readPlanVote(@PathVariable Long planVoteId);

    @Operation(summary = "PlanVote(투표장) 전체/목록 조회", description = "page는 1부터")
    ResponseEntity readPlanVoteList(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "20") int size,
                                    @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                    @RequestParam(required = false, defaultValue = "false") boolean isAsc);

    @Operation(summary = "PlanVote(투표장) 수정", description = " ")
    ResponseEntity updatePlanVote(@PathVariable Long voteId, @Validated(PlanValidationSequence.class) @RequestBody PlanVoteDto.Update request);

    @Operation(summary = "PlanVote(투표장) 종료", description = "Patch(o) Put(x)")
    ResponseEntity closePlanVote(@PathVariable Long voteId);

    @Operation(summary = "PlanVote(투표장) 삭제", description = " ")
    ResponseEntity deletePlanVote(@PathVariable Long voteId);










    @Operation(summary = "VotePaper(투표용지) 생성", description = "isVotedA가 true면 A에 투표, false면 B에 투표, content는 혹시나 나중의 투표 추가기능: 예를들어 투표사유를 적는다던가, MBTI를 적는다던가")
    ResponseEntity createVotePaper(@Validated(PlanValidationSequence.class) @RequestBody VotePaperDto.Create request);

    @Operation(summary = "VotePaper(투표용지) 상세단일 조회", description = " ")
    ResponseEntity readVotePaper(@PathVariable Long votePaperId);

    @Operation(summary = "VotePaper(투표용지) 유저별 전체목록 조회", description = "page는 1부터")
    ResponseEntity readVotePaperList(@RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "20") int size,
                                     @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                     @RequestParam(required = false, defaultValue = "false") boolean isAsc);

    @Operation(summary = "VotePaper(투표용지) 수정", description = " ")
    ResponseEntity updateVotePaper(@PathVariable Long votePaperId, @Validated(PlanValidationSequence.class) @RequestBody VotePaperDto.Update request);

    @Operation(summary = "VotePaper(투표용지) 삭제", description = " ")
    ResponseEntity deleteVotePaper(@PathVariable Long votePaperId);










//    @Operation(summary = "Plan 댓글 등록", description = " ")
//    ResponseEntity createPlanComment(@PathVariable Long planId, @RequestBody PlanCommentDto.Create request);
//
//    @Operation(summary = "Plan 댓글 조회", description = "page 는 1부터")
//    ResponseEntity readPlanCommentList(@RequestParam(defaultValue = "1") int page,
//                               @RequestParam(defaultValue = "20") int size,
//                               @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
//                               @RequestParam(required = false, defaultValue = "false") boolean isAsc);
//
//    @Operation(summary = "Plan 댓글 수정", description = " ")
//    ResponseEntity updatePlanComment(@PathVariable Long planId, @PathVariable Long commentId, @RequestBody PlanCommentDto.Update request);
//
//    @Operation(summary = "Plan 댓글 삭제", description = " ")
//    ResponseEntity deletePlanComment(@PathVariable Long planId, @PathVariable Long commentId);










    @Operation(summary = "Plan 좋아요 등록", description = "선택한 Plan 좋아요를 등록하는 API")
    ResponseEntity createPlanLike(@PathVariable Long planId) ;

    @Operation(summary = "Plan 좋아요 취소", description = "선택한 Plan 좋아요를 취소하는 API")
    ResponseEntity deletePlanLike(@PathVariable Long planId);

    @Operation(summary = "Plan 좋아요 전체목록 조회", description = "좋아요을 누른 Plan 목록을 페이지별로 조회하는 API")
    ResponseEntity getPlanLikeList(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "20") int size,
                                   @AuthenticationPrincipal UserDetailsImpl userDetails);

    @Operation(summary = "Plan 스크랩 등록", description = "선택한 Plan 스크랩에 추가하는 API")
    ResponseEntity createPlanScrap(@PathVariable Long planId);

    @Operation(summary = "Plan 스크랩 취소", description = "선택한 Plan 스크랩에서 삭제하는 API")
    ResponseEntity deletePlanScrap(@PathVariable Long planId);

    @Operation(summary = "Plan 스크랩 전체목록 조회", description = "스크랩한 Plan 목록을 페이지별로 조회하는 API")
    ResponseEntity getPlanScrapList(@RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "20") int size, @AuthenticationPrincipal UserDetailsImpl userDetails);










    @Operation(summary = "(성찬전용) HTTPS 기능", description = "HTTPS 수신상태가 양호함을 AWS 와 통신하는 API")
    String healthcheck();
}