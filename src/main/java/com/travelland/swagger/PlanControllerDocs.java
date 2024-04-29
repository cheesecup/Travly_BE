package com.travelland.swagger;

import com.travelland.valid.plan.PlanValidationSequence;
import com.travelland.dto.plan.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "여행 플랜 API", description = "여행 전 플랜 관련 API")
public interface PlanControllerDocs {

    @Operation(summary = "Plan 한방 작성", description = "3계층구조, Plan 1개 ⊃ DayPlan N개 ⊃ UnitPlan M개")
    ResponseEntity createPlanAllInOne(@RequestBody PlanDto.CreateAllInOne request);

    @Operation(summary = "Plan 한방 단일상세 조회", description = " ")
    ResponseEntity readPlanAllInOne(@PathVariable Long planId);

    @Operation(summary = "Plan 전체목록 조회", description = "page는 1부터, 예시: /plans?page=1&size=20&sortBy=createdAt&isAsc=false")
    ResponseEntity readPlanList(@RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "20") int size,
                                @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                @RequestParam(required = false, defaultValue = "false") boolean isAsc);

    @Operation(summary = "(마이페이지용) Plan 유저별 전체목록 조회", description = "page는 1부터")
    ResponseEntity readPlanListForMember(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "20") int size,
                                         @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                         @RequestParam(required = false, defaultValue = "false") boolean isAsc);

    @Operation(summary = "Plan 한방 수정", description = " ")
    ResponseEntity updatePlanAllInOne(@PathVariable Long planId, @RequestBody PlanDto.UpdateAllInOne request);

    @Operation(summary = "Plan 한방 삭제", description = " ")
    ResponseEntity deletePlanAllInOne(@PathVariable Long planId);










    @Operation(summary = "PlanVote(투표장) 생성", description = "투표기간(Duration): { 1분(ONE_MINUTE), 1초(ONE_SECOND), 12시간(HALF_DAY), 1일(ONE_DAY), 3일(THREE_DAY), 7일(SEVEN_DAY) }")
    ResponseEntity createPlanVote(@Validated(PlanValidationSequence.class) @RequestBody PlanVoteDto.Create request);

    @Operation(summary = "PlanVote(투표장) 단일상세 조회", description = " ")
    ResponseEntity readPlanVote(@PathVariable Long planVoteId);

    @Operation(summary = "PlanVote(투표장) 전체목록 조회", description = "page는 1부터")
    ResponseEntity readPlanVoteList(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "20") int size,
                                    @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                    @RequestParam(required = false, defaultValue = "false") boolean isAsc);

    @Operation(summary = "(마이페이지용) PlanVote(투표장) 유저별 전체목록 조회", description = "page는 1부터")
    ResponseEntity readPlanVoteListForMember(@RequestParam(defaultValue = "1") int page,
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

    @Operation(summary = "VotePaper(투표용지) 단일상세 조회", description = " ")
    ResponseEntity readVotePaper(@PathVariable Long votePaperId);

    @Operation(summary = "(마이페이지용) VotePaper(투표용지) 유저별 전체목록 조회", description = "page는 1부터")
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
//    @Operation(summary = "Plan 댓글 전체목록 조회", description = "page 는 1부터")
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

    @Operation(summary = "(마이페이지용) Plan 좋아요 유저별 전체목록 조회", description = "좋아요을 누른 Plan 목록을 페이지별로 조회하는 API")
    ResponseEntity getPlanLikeList(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Plan 스크랩 등록", description = "선택한 Plan 스크랩에 추가하는 API")
    ResponseEntity createPlanScrap(@PathVariable Long planId);

    @Operation(summary = "Plan 스크랩 취소", description = "선택한 Plan 스크랩에서 삭제하는 API")
    ResponseEntity deletePlanScrap(@PathVariable Long planId);

    @Operation(summary = "(마이페이지용) Plan 스크랩 유저별 전체목록 조회", description = "스크랩한 Plan 목록을 페이지별로 조회하는 API")
    ResponseEntity getPlanScrapList(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size);










    @Operation(summary = "(여행후기용) Plan->Trip 데이터 변환", description = " ")
    ResponseEntity transferPlanToTrip(@PathVariable Long planId);










    @Operation(summary = "(AWS 로드밸런서용) 상태코드200 체크로 살아있는 서버에게만 분산처리)", description = "무조건 200을 보냄 = 서버가 죽으면 200을 못 보냄")
    String healthcheck();
}