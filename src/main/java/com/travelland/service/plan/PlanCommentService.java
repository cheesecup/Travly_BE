package com.travelland.service.plan;

import com.travelland.domain.member.Member;
import com.travelland.domain.plan.Plan;
import com.travelland.domain.plan.PlanComment;
import com.travelland.dto.plan.PlanCommentDto;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.global.security.UserDetailsImpl;
import com.travelland.repository.plan.PlanCommentRepository;
import com.travelland.repository.plan.PlanRepository;
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
public class PlanCommentService {

    private final PlanRepository planRepository;
    private final PlanCommentRepository planCommentRepository;

    // Plan 댓글 등록
    public PlanCommentDto.Id createPlanComment(Long planId, PlanCommentDto.Create request) {
        PlanComment savedPlanComment = planCommentRepository.save(new PlanComment(request, getLoginMember(), getPlan(planId)));
        return new PlanCommentDto.Id(savedPlanComment);
    }

    // Plan 댓글 전체목록 조회
    public Page<PlanCommentDto.Get> readPlanCommentList(Long planId, int page, int size, String sortBy, boolean isAsc) {
        Page<PlanComment> planComments = planCommentRepository.findAllByPlanIdAndIsDeleted(getPageable(page, size, sortBy, isAsc), planId, false);
        return planComments.map(PlanCommentDto.Get::new);
    }

    // Plan 댓글 수정
    public PlanCommentDto.Id updatePlanComment(Long commentId, PlanCommentDto.Update request) {
        PlanComment planComment = planCommentRepository.findByIdAndIsDeleted(commentId, false).orElseThrow(() -> new CustomException(ErrorCode.PLAN_COMMENT_NOT_FOUND));
        checkAuth(getLoginMember().getId(), planComment.getMember().getId(), ErrorCode.POST_UPDATE_NOT_PERMISSION);
        planComment.update(request);
        return new PlanCommentDto.Id(planComment);
    }

    // Plan 댓글 삭제
    public PlanCommentDto.Delete deletePlanComment(Long commentId) {
        PlanComment planComment = planCommentRepository.findByIdAndIsDeleted(commentId, false).orElseThrow(() -> new CustomException(ErrorCode.PLAN_COMMENT_NOT_FOUND));
        checkAuth(getLoginMember().getId(), planComment.getMember().getId(), ErrorCode.POST_DELETE_NOT_PERMISSION);
        planComment.delete();
        return new PlanCommentDto.Delete(planComment.getIsDeleted());
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
