package com.travelland.controller;

import com.travelland.dto.plan.*;
import com.travelland.global.security.UserDetailsImpl;
import com.travelland.service.plan.*;
import com.travelland.swagger.PlanControllerDocs;
import com.travelland.valid.plan.PlanValidationSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class PlanController implements PlanControllerDocs {

    private final PlanService planService;
    private final PlanVoteService planVoteService;
    private final PlanCommentService planCommentService;
    private final PlanLikeService planLikeService;
    private final PlanScrapService planScrapService;
    private final PlanInviteService planInviteService;
    private final PlanToTripService planToTripService;

    // Plan 한방 작성
    @PostMapping("/plans/allInOn")
    public ResponseEntity<PlanDto.Id> createPlanAllInOne(@Validated(PlanValidationSequence.class) @RequestBody PlanDto.CreateAllInOne request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(planService.createPlanAllInOne(request));
    }

    // Plan 한방 단일상세 조회
    @GetMapping("/plans/allInOn/{planId}")
    public ResponseEntity<PlanDto.GetAllInOne> readPlanAllInOne(@PathVariable Long planId) {
        return ResponseEntity.status(HttpStatus.OK).body(planService.readPlanAllInOne(planId));
    }

    // Plan 전체목록 조회
    @GetMapping("/plans")
    public ResponseEntity<Page<PlanDto.Get>> readPlanList(@RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "10") int size,
                                                          @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                                          @RequestParam(required = false, defaultValue = "false") boolean isAsc) {
        return ResponseEntity.status(HttpStatus.OK).body(planService.readPlanList(page, size, sortBy, isAsc));
    }

    // (마이페이지용) Plan 유저별 전체목록 조회
    @GetMapping("/users/plans")
    public ResponseEntity<Page<PlanDto.Get>> readPlanListForMember(@RequestParam(defaultValue = "1") int page,
                                                                   @RequestParam(defaultValue = "10") int size,
                                                                   @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                                                   @RequestParam(required = false, defaultValue = "false") boolean isAsc) {
        return ResponseEntity.status(HttpStatus.OK).body(planService.readPlanListForMember(page, size, sortBy, isAsc));
    }

    // Plan 한방 수정
    @PutMapping("/plans/allInOn/{planId}")
    public ResponseEntity<PlanDto.Id> updatePlanAllInOne(@PathVariable Long planId, @Validated(PlanValidationSequence.class) @RequestBody PlanDto.UpdateAllInOne request) {
        return ResponseEntity.status(HttpStatus.OK).body(planService.updatePlanAllInOne(planId, request));
    }

    // Plan 한방 삭제
    @DeleteMapping("/plans/allInOn/{planId}")
    public ResponseEntity<PlanDto.Delete> deletePlanAllInOne(@PathVariable Long planId) {
        return ResponseEntity.status(HttpStatus.OK).body(planService.deletePlanAllInOne(planId));
    }










    // PlanVote(투표장) 생성
    @PostMapping("/votes")
    public ResponseEntity<PlanVoteDto.Id> createPlanVote(@Validated(PlanValidationSequence.class) @RequestBody PlanVoteDto.Create request) {
        PlanVoteDto.Id response = planVoteService.createPlanVote(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // PlanVote(투표장) 단일상세 조회
    @GetMapping("/votes/{voteId}")
    public ResponseEntity<PlanVoteDto.Get> readPlanVote(@PathVariable Long voteId) {
        PlanVoteDto.Get response = planVoteService.readPlanVote(voteId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // PlanVote(투표장) 전체목록 조회
    @GetMapping("/votes")
    public ResponseEntity<Page<PlanVoteDto.Get>> readPlanVoteList(@RequestParam(defaultValue = "1") int page,
                                                                  @RequestParam(defaultValue = "10") int size,
                                                                  @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                                                  @RequestParam(required = false, defaultValue = "false") boolean isAsc) {
        Page<PlanVoteDto.Get> response = planVoteService.readPlanVoteList(page, size, sortBy, isAsc);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // (마이페이지용) PlanVote(투표장) 유저별 전체목록 조회
    @GetMapping("/users/votes")
    public ResponseEntity<Page<PlanVoteDto.Get>> readPlanVoteListForMember(@RequestParam(defaultValue = "1") int page,
                                                                           @RequestParam(defaultValue = "10") int size,
                                                                           @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                                                           @RequestParam(required = false, defaultValue = "false") boolean isAsc) {
        Page<PlanVoteDto.Get> response = planVoteService.readPlanVoteListForMember(page, size, sortBy, isAsc);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // PlanVote(투표장) 수정
    @PutMapping("/votes/{voteId}")
    public ResponseEntity<PlanVoteDto.Id> updatePlanVote(@PathVariable Long voteId, @Validated(PlanValidationSequence.class) @RequestBody PlanVoteDto.Update request) {
        PlanVoteDto.Id response = planVoteService.updatePlanVote(voteId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // PlanVote(투표장) 종료
    @PatchMapping("/votes/{voteId}")
    public ResponseEntity<PlanVoteDto.Close> closePlanVote(@PathVariable Long voteId) {
        PlanVoteDto.Close response = planVoteService.closePlanVote(voteId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // PlanVote(투표장) 삭제
    @DeleteMapping("/votes/{voteId}")
    public ResponseEntity<PlanVoteDto.Delete> deletePlanVote(@PathVariable Long voteId) {
        PlanVoteDto.Delete response = planVoteService.deletePlanVote(voteId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }










    // VotePaper(투표용지) 생성
    @PostMapping("/votePapers")
    public ResponseEntity<VotePaperDto.Id> createVotePaper(@Validated(PlanValidationSequence.class) @RequestBody VotePaperDto.Create request) {
        VotePaperDto.Id response = planVoteService.createVotePaper(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // VotePaper(투표용지) 단일상세 조회
    @GetMapping("/votePapers/{votePaperId}")
    public ResponseEntity<VotePaperDto.Get> readVotePaper(@PathVariable Long votePaperId) {
        VotePaperDto.Get response = planVoteService.readVotePaper(votePaperId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // (마이페이지용) VotePaper(투표용지) 유저별 전체목록 조회
    @GetMapping("/users/votePapers")
    public ResponseEntity<Page<VotePaperDto.Get>> readVotePaperList(@RequestParam(defaultValue = "1") int page,
                                                                    @RequestParam(defaultValue = "10") int size,
                                                                    @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                                                    @RequestParam(required = false, defaultValue = "false") boolean isAsc) {
        Page<VotePaperDto.Get> response = planVoteService.readVotePaperList(page, size, sortBy, isAsc);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // VotePaper(투표용지) 수정
    @PutMapping("/votePapers/{votePaperId}")
    public ResponseEntity<VotePaperDto.Id> updateVotePaper(@PathVariable Long votePaperId, @Validated(PlanValidationSequence.class) @RequestBody VotePaperDto.Update request) {
        VotePaperDto.Id response = planVoteService.updateVotePaper(votePaperId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // VotePaper(투표용지) 삭제
    @DeleteMapping("/votePapers/{votePaperId}")
    public ResponseEntity<VotePaperDto.Delete> deleteVotePaper(@PathVariable Long votePaperId) {
        VotePaperDto.Delete response = planVoteService.deleteVotePaper(votePaperId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }










//    // Plan 댓글 등록
//    @PostMapping("/plans/{planId}/comments")
//    public ResponseEntity<PlanCommentDto.Id> createPlanComment(@PathVariable Long planId, @Validated(PlanValidationSequence.class) @RequestBody PlanCommentDto.Create request) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(planCommentService.createPlanComment(planId, request));
//    }
//
//    // Plan 댓글 전체목록 조회
//    @GetMapping("/plans/{planId}/comments") // 예시: /plans/{planId}/comments?page=1&size=20&sortBy=createdAt&isAsc=false, page 는 1부터
//    public ResponseEntity<Page<PlanCommentDto.Get>> readPlanCommentList(@RequestParam(defaultValue = "1") int page,
//                                                                        @RequestParam(defaultValue = "10") int size,
//                                                                        @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
//                                                                        @RequestParam(required = false, defaultValue = "false") boolean isAsc) {
//        return ResponseEntity.status(HttpStatus.OK).body(planCommentService.readPlanCommentList(planId, page, size, sortBy, isAsc));
//    }
//
//    // Plan 댓글 수정
//    @PutMapping("/plans/{planId}/comments/{commentId}")
//    public ResponseEntity<PlanCommentDto.Id> updatePlanComment(@PathVariable Long planId, @PathVariable Long commentId, @Validated(PlanValidationSequence.class) @RequestBody PlanCommentDto.Update request) {
//        return ResponseEntity.status(HttpStatus.OK).body(planCommentService.updatePlanComment(commentId, request));
//    }
//
//    // Plan 댓글 삭제
//    @DeleteMapping("/plans/{planId}/comments/{commentId}")
//    public ResponseEntity<PlanCommentDto.Delete> deletePlanComment(@PathVariable Long planId, @PathVariable Long commentId) {
//        return ResponseEntity.status(HttpStatus.OK).body(planCommentService.deletePlanComment(commentId));
//    }










    // Plan 좋아요 등록
    @PostMapping("/plans/{planId}/like")
    public ResponseEntity<PlanLikeScrapDto.Result> createPlanLike(@PathVariable Long planId) {
        planLikeService.registerPlanLike(planId);
        return ResponseEntity.status(HttpStatus.OK).body(new PlanLikeScrapDto.Result(true));
    }

    // Plan 좋아요 취소
    @DeleteMapping("/plans/{planId}/like")
    public ResponseEntity<PlanLikeScrapDto.Result> deletePlanLike(@PathVariable Long planId) {
        planLikeService.cancelPlanLike(planId);
        return ResponseEntity.status(HttpStatus.OK).body(new PlanLikeScrapDto.Result(false));
    }

    // (마이페이지용) Plan 좋아요 전체목록 조회
    @GetMapping("/plans/like")
    public ResponseEntity<List<PlanLikeScrapDto.GetList>> getPlanLikeList(@RequestParam(defaultValue = "1") int page,
                                                                 @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(planLikeService.getPlanLikeList(page, size));
    }

    // Plan 스크랩 등록
    @PostMapping("/plans/{planId}/scrap")
    public ResponseEntity<PlanLikeScrapDto.Result> createPlanScrap(@PathVariable Long planId) {
        planScrapService.registerPlanScrap(planId);
        return ResponseEntity.status(HttpStatus.OK).body(new PlanLikeScrapDto.Result(true));
    }

    // Plan 스크랩 취소
    @DeleteMapping("/plans/{planId}/scrap")
    public ResponseEntity<PlanLikeScrapDto.Result> deletePlanScrap(@PathVariable Long planId) {
        planScrapService.cancelPlanScrap(planId);
        return ResponseEntity.status(HttpStatus.OK).body(new PlanLikeScrapDto.Result(false));
    }

    // (마이페이지용) Plan 스크랩 전체목록 조회
    @GetMapping("/plans/scrap")
    public ResponseEntity<List<PlanLikeScrapDto.GetList>> getPlanScrapList(@RequestParam(defaultValue = "1") int page,
                                                                  @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(planScrapService.getPlanScrapList(page, size));
    }










    // (여행후기용) Plan->Trip 데이터 변환
    @GetMapping("/planToTrip/{planId}")
    public ResponseEntity<PlanToTripDto> transferPlanToTrip(@PathVariable Long planId) {
        return ResponseEntity.status(HttpStatus.OK).body(planToTripService.transferPlanToTrip(planId));
    }










    // Plan 초대
    @PostMapping("/plans/{planId}/invite")
    public ResponseEntity<?> invitePlan(@PathVariable Long planId,
                                        @RequestBody PlanDto.Invitee invitee) {
        planInviteService.invitePlan(planId, invitee);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // Plan 초대 수락
    @PostMapping("/plans/{planId}/invite/agree")
    public ResponseEntity<?> agreeInvitedPlan(@PathVariable Long planId,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        planInviteService.agreeInvitedPlan(planId, userDetails.getMember());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // Plan 초대 거절
    @PostMapping("/plans/{planId}/invite/disagree")
    public ResponseEntity<?> disagreeInvitedPlan(@PathVariable Long planId,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        planInviteService.disagreeInvitedPlan(planId, userDetails.getMember());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // Plan 초대 멤버 삭제
    @DeleteMapping("/plans/{planId}/invite")
    public ResponseEntity<?> deleteInvitedMember(@PathVariable Long planId,
                                                 @RequestBody PlanDto.Invitee invitee) {
        planInviteService.deleteInvitedMember(planId, invitee);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // (마이페이지용) Plan 초대 목록 조회
    @GetMapping("/users/plans/invite")
    public ResponseEntity<Page<PlanDto.Get>> readPlanListForInvitee(@RequestParam int page,
                                                    @RequestParam int size,
                                                    @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(planInviteService.readPlanListForInvitee(page, size, userDetails.getMember()));
    }










    // (AWS 로드밸런서용) 상태코드200 체크로 살아있는 서버에게만 분산처리
    @GetMapping("/healthcheck")
    public String healthcheck() {
        return "OK";
    }
}
