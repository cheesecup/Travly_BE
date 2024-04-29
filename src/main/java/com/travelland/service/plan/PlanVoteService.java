package com.travelland.service.plan;

import com.travelland.domain.member.Member;
import com.travelland.domain.plan.Plan;
import com.travelland.domain.plan.PlanVote;
import com.travelland.domain.plan.VotePaper;
import com.travelland.dto.plan.PlanVoteDto;
import com.travelland.dto.plan.VotePaperDto;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.global.security.UserDetailsImpl;
import com.travelland.repository.member.MemberRepository;
import com.travelland.repository.plan.PlanRepository;
import com.travelland.repository.plan.PlanVoteRepository;
import com.travelland.repository.plan.VotePaperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PlanVoteService {

    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;
    private final PlanVoteRepository planVoteRepository;
    private final VotePaperRepository votePaperRepository;

    // PlanVote(투표장) 생성
    public PlanVoteDto.Id createPlanVote(PlanVoteDto.Create request) {
        Member member = getLoginMember();
        Plan planA = getPlan(request.getPlanAId());
        Plan planB = getPlan(request.getPlanBId());
        PlanVote savedPlanVote = planVoteRepository.save(new PlanVote(request, planA, planB, member));
        return new PlanVoteDto.Id(savedPlanVote);
    }

    // PlanVote(투표장) 단일상세 조회
    public PlanVoteDto.Get readPlanVote(Long planVoteId) {
        PlanVote planVote = planVoteRepository.findByIdAndIsDeleted(planVoteId, false).orElseThrow(() -> new CustomException(ErrorCode.PLAN_VOTE_NOT_FOUND));
        planVote.checkTimeOut(); // 투표기간이 종료됐는지 체크
        return new PlanVoteDto.Get(planVote);
    }

    // PlanVote(투표장) 전체목록 조회: page는 1부터
    public Page<PlanVoteDto.Get> readPlanVoteList(int page, int size, String sortBy, boolean isAsc) {
        planVoteRepository.findAllByIsDeletedAndIsClosed(false, false).forEach(PlanVote::checkTimeOut); // 투표기간이 종료됐는지 체크
        Page<PlanVote> planVotes = planVoteRepository.findAllByIsDeleted(getPageable(page, size, sortBy, isAsc), false);
        return planVotes.map(PlanVoteDto.Get::new);
    }

    // (마이페이지용) PlanVote(투표장) 유저별 전체목록 조회: page는 1부터
    public Page<PlanVoteDto.Get> readPlanVoteListForMember(int page, int size, String sortBy, boolean isAsc) {
        planVoteRepository.findAllByIsDeletedAndIsClosed(false, false).forEach(PlanVote::checkTimeOut); // 투표기간이 종료됐는지 체크
        Page<PlanVote> planVotes = planVoteRepository.findAllByIsDeletedAndMemberId(getPageable(page, size, sortBy, isAsc), false, getLoginMember().getId());
        return planVotes.map(PlanVoteDto.Get::new);
    }

    // PlanVote(투표장) 수정
    public PlanVoteDto.Id updatePlanVote(Long planVoteId, PlanVoteDto.Update request) {
        PlanVote planVote = planVoteRepository.findByIdAndIsDeletedAndIsClosed(planVoteId, false, false).orElseThrow(() -> new CustomException(ErrorCode.PLAN_VOTE_NOT_FOUND));

        // 투표기간이 종료됐는지 체크
        if (planVote.checkTimeOut()) {
            throw new CustomException(ErrorCode.PLAN_VOTE_IS_CLOSED);
        }

        checkAuth(getLoginMember().getId(), planVote.getMember().getId(), ErrorCode.POST_UPDATE_NOT_PERMISSION);
        Plan planA = getPlan(request.getPlanAId());
        Plan planB = getPlan(request.getPlanBId());
        planVote.update(request, planA, planB);
        return new PlanVoteDto.Id(planVote);
    }

    // PlanVote(투표장) 종료
    public PlanVoteDto.Close closePlanVote(Long planVoteId) {
        PlanVote planVote = planVoteRepository.findByIdAndIsDeleted(planVoteId, false).orElseThrow(() -> new CustomException(ErrorCode.PLAN_VOTE_NOT_FOUND));
        checkAuth(getLoginMember().getId(), planVote.getMember().getId(), ErrorCode.POST_UPDATE_NOT_PERMISSION);
        planVote.close();
        return new PlanVoteDto.Close(planVote.getIsClosed());
    }

    // PlanVote(투표장) 삭제
    public PlanVoteDto.Delete deletePlanVote(Long planVoteId) {
        PlanVote planVote = planVoteRepository.findById(planVoteId).orElseThrow(() -> new CustomException(ErrorCode.PLAN_VOTE_NOT_FOUND));
        checkAuth(getLoginMember().getId(), planVote.getMember().getId(), ErrorCode.POST_DELETE_NOT_PERMISSION);
        planVote.delete();
        return new PlanVoteDto.Delete(planVote.getIsDeleted());
    }










    // VotePaper(투표용지) 생성
    public VotePaperDto.Id createVotePaper(VotePaperDto.Create request) {
        // 유저테스트 한정, 비로그인 시에도 더미계정으로 투표가 가능하도록 기획
        Member member;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            member = ((UserDetailsImpl) authentication.getPrincipal()).getMember();
        } else {
            member = getMember("test@test.com");
        }

        // 투표기간이 종료됐는지 체크
        PlanVote planVote = planVoteRepository.findByIdAndIsDeleted(request.getPlanVoteId(), false).orElseThrow(() -> new CustomException(ErrorCode.PLAN_VOTE_NOT_FOUND));
        if (planVote.checkTimeOut()) {
            throw new CustomException(ErrorCode.PLAN_VOTE_IS_CLOSED);
        }

        // 이미 투표한적이 있는지 불러옴, 투표한적이 없으면 Null, 있다면 createdAt 기준 가장최근 투표용지를 불러옴
//        Optional<VotePaper> recentVotePaper = votePaperRepository.findFirstByMemberIdAndPlanVoteIdOrderByCreatedAtDesc(member.getId(), request.getPlanVoteId());
//        recentVotePaper.ifPresent(VotePaper::checkReVoteAble);

        // 생성된 투표내용에 따라 투표 Count 증가 반영
        if (request.getIsVotedA() == true) {
            planVote.increaseAVoteCount();
        } else if (request.getIsVotedA() == false) {
            planVote.increaseBVoteCount();
        }

        VotePaper savedVotePaper = votePaperRepository.save(new VotePaper(request, member.getId()));
        return new VotePaperDto.Id(savedVotePaper);
    }

    // VotePaper(투표용지) 단일상세 조회
    public VotePaperDto.Get readVotePaper(Long votePaperId) {
        VotePaper votePaper = votePaperRepository.findByIdAndIsDeleted(votePaperId, false).orElseThrow(() -> new CustomException(ErrorCode.VOTE_PAPER_NOT_FOUND));
        return new VotePaperDto.Get(votePaper);
    }

    // (마이페이지용) VotePaper(투표용지) 유저별 전체목록 조회: page는 1부터
    public Page<VotePaperDto.Get> readVotePaperList(int page, int size, String sortBy, boolean isAsc) {
        Page<VotePaper> votePapers = votePaperRepository.findAllByIsDeletedAndMemberId(getPageable(page, size, sortBy, isAsc), false, getLoginMember().getId());
        return votePapers.map(VotePaperDto.Get::new);
    }

    // VotePaper(투표용지) 수정
    public VotePaperDto.Id updateVotePaper(Long votePaperId, VotePaperDto.Update request) {
        PlanVote planVote = planVoteRepository.findByIdAndIsDeleted(request.getPlanVoteId(), false).orElseThrow(() -> new CustomException(ErrorCode.PLAN_VOTE_NOT_FOUND));
        VotePaper votePaper = votePaperRepository.findByIdAndIsDeleted(votePaperId, false).orElseThrow(() -> new CustomException(ErrorCode.VOTE_PAPER_NOT_FOUND));
        checkAuth(getLoginMember().getId(), votePaper.getMemberId(), ErrorCode.POST_UPDATE_NOT_PERMISSION);

        // 투표기간이 종료됐는지 체크
        if (planVote.checkTimeOut()) {
            throw new CustomException(ErrorCode.PLAN_VOTE_IS_CLOSED);
        }

        // 수정된 투표내용에 따라 투표 Count 증/감 반영
        if (request.getIsVotedA() != votePaper.getIsVotedA()) {
            if (request.getIsVotedA() == true) {
                planVote.changeBtoAVoteCount();
            } else if (request.getIsVotedA() == false) {
                planVote.changeAtoBVoteCount();
            }
        }

        votePaper.update(request);
        return new VotePaperDto.Id(votePaper);
    }

    // VotePaper(투표용지) 삭제
    public VotePaperDto.Delete deleteVotePaper(Long votePaperId) {
        VotePaper votePaper = votePaperRepository.findByIdAndIsDeleted(votePaperId, false).orElseThrow(() -> new CustomException(ErrorCode.VOTE_PAPER_NOT_FOUND));
        PlanVote planVote = planVoteRepository.findByIdAndIsDeleted(votePaper.getPlanVoteId(), false).orElseThrow(() -> new CustomException(ErrorCode.PLAN_VOTE_NOT_FOUND));
        checkAuth(getLoginMember().getId(), votePaper.getMemberId(), ErrorCode.POST_DELETE_NOT_PERMISSION);

        // 삭제된 투표내용에 따라 투표 Count 감소 반영
        if (votePaper.getIsDeleted() == false) { // 이미 삭제된 경우 재반영 않도록 조치
            if (votePaper.getIsVotedA() == true) {
                planVote.decreaseAVoteCount();
            } else if (votePaper.getIsVotedA() == false) {
                planVote.decreaseBVoteCount();
            }
        }

        votePaper.delete();
        return new VotePaperDto.Delete(votePaper.getIsDeleted());
    }










    private Member getLoginMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            Member member = ((UserDetailsImpl)authentication.getPrincipal()).getMember();
            return member;
        } else {
            throw new CustomException(ErrorCode.STATUS_NOT_LOGIN);
        }
    }

    private Member getMember (String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private Plan getPlan(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));
    }

    private void checkAuth(Long loginMemberId, Long writerId, ErrorCode errorCode) {
        if (loginMemberId != writerId) {
            throw new CustomException(errorCode);
        }
    }

    private Pageable getPageable(int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page-1, size, sort);
        return pageable;
    }
}
