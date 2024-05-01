package com.travelland.service.plan;

import com.travelland.constant.NotificationType;
import com.travelland.domain.Notification;
import com.travelland.domain.member.Member;
import com.travelland.domain.plan.Plan;
import com.travelland.domain.plan.PlanInvite;
import com.travelland.dto.plan.PlanDto;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.global.notification.DoEvent;
import com.travelland.repository.notification.NotificationRepository;
import com.travelland.repository.plan.PlanInviteRepository;
import com.travelland.repository.plan.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlanInviteService {

    private final PlanInviteRepository planInviteRepository;
    private final PlanRepository planRepository;
    private final NotificationRepository notificationRepository;

    private final ApplicationEventPublisher eventPublisher;

    // 플랜 초대
    @Transactional
    public void invitePlan(Long planId, PlanDto.Invitee invitee) {
        if (planInviteRepository.findByPlanIdAndMemberEmail(planId, invitee.getEmail()).isEmpty()) {
            eventPublisher.publishEvent(new DoEvent.DoInviteEvent(this, planId, invitee.getEmail()));
        }
    }

    // 플랜 초대 수락
    @Transactional
    public void agreeInvitedPlan(Long planId, Member member) {
        if (planInviteRepository.findByPlanIdAndMember(planId, member).isPresent())
            return;

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));
        planInviteRepository.save(new PlanInvite(member, plan));

        Optional<Notification> notification = notificationRepository
                .findByNotificationTypeAndReceiverAndTitle(NotificationType.INVITE, member, plan.getTitle());
        notification.ifPresent(Notification::setIsRead);
        eventPublisher.publishEvent(new DoEvent.DoAgreeEvent(this, planId, member.getNickname()));
    }

    // 플랜 초대 거절
    @Transactional
    public void disagreeInvitedPlan(Long planId, Member member) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));
        Optional<Notification> notification = notificationRepository
                .findByNotificationTypeAndReceiverAndTitle(NotificationType.INVITE, member, plan.getTitle());
        notification.ifPresent(Notification::setIsRead);
        eventPublisher.publishEvent(new DoEvent.DoDisagreeEvent(this, planId, member.getNickname()));
    }

    // 플랜 초대 멤버 삭제
    @Transactional
    public void deleteInvitedMember(Long planId, PlanDto.Invitee invitee) {
        Optional<PlanInvite> planInvite = planInviteRepository.findByPlanIdAndMemberEmail(planId, invitee.getEmail());
        planInvite.ifPresent(planInviteRepository::delete);
    }

    // 초대된 플랜 목록 조회
    @Transactional(readOnly = true)
    public Page<PlanDto.Get> readPlanListForInvitee(int page, int size, Member member) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<PlanInvite> pageOfPlanInvites = planInviteRepository.findByMemberNickname(pageable, member.getNickname());
        List<PlanInvite> filteredPlans = pageOfPlanInvites.filter(planInvite -> !planInvite.getPlan().getIsDeleted()).toList();
        List<PlanDto.Get> pageOfPlanDtoGets = filteredPlans.stream().map(planInvite -> new PlanDto.Get(planInvite.getPlan())).toList();

        return new PageImpl<>(pageOfPlanDtoGets, pageable, filteredPlans.size());
    }

}
