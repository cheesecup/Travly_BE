package com.travelland.global.notification;

import com.travelland.constant.NotificationType;
import com.travelland.domain.member.Member;
import com.travelland.domain.plan.Plan;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.repository.member.MemberRepository;
import com.travelland.repository.plan.PlanRepository;
import com.travelland.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationSender {

    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;
    private final NotificationService notificationService;

    private final String BASE_FRONT_URL = "https://www.travly.site";

    @TransactionalEventListener
    @Async
    public void handleDoInvite(DoEvent.DoInviteEvent event) {
        Plan plan = planRepository.findById(event.getPlanId()).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));
        String title = plan.getTitle();
        String content = "플랜에 초대되었습니다. 수락하시겠습니까?";
        String url = BASE_FRONT_URL + "/planDetail/" + event.getPlanId();

        Member receiver = memberRepository.findByEmail(event.getInvitee()).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        notificationService.send(receiver, title, content, url, NotificationType.INVITE);
    }

    @TransactionalEventListener
    @Async
    public void handleDoAgree(DoEvent.DoAgreeEvent event) {
        Plan plan = planRepository.findById(event.getPlanId()).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));
        String title = plan.getTitle();
        String content = event.getInviteeNickname() + "님이 플랜 초대를 수락하였습니다.";
        String url = BASE_FRONT_URL + "/planDetail/" + event.getPlanId();

        notificationService.send(plan.getMember(), title, content, url, NotificationType.AGREE);
    }

    @TransactionalEventListener
    @Async
    public void handleDoDisagree(DoEvent.DoDisagreeEvent event) {
        Plan plan = planRepository.findById(event.getPlanId()).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));
        String title = plan.getTitle();
        String content = event.getInviteeNickname() + "님이 플랜 초대를 거절하였습니다.";
        String url = BASE_FRONT_URL + "/planDetail/" + event.getPlanId();

        notificationService.send(plan.getMember(), title, content, url, NotificationType.DISAGREE);
    }

    @TransactionalEventListener
    @Async
    public void handleDoVote(DoEvent.DoVoteEvent event) {
        Plan plan = planRepository.findById(event.getPlanId()).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));
        String title = plan.getTitle();
        String content = "에 투표할 수 있습니다.";
        String url = BASE_FRONT_URL + "/planDetail/" + event.getPlanId();

        Member receiver = memberRepository.findByEmail(event.getInvitee()).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        notificationService.send(receiver, title, content, url, NotificationType.VOTE);
    }
}
