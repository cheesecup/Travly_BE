package com.travelland.global.notify;

import com.travelland.constant.NotificationType;
import com.travelland.domain.member.Member;
import com.travelland.domain.plan.Plan;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.repository.member.MemberRepository;
import com.travelland.repository.plan.PlanRepository;
import com.travelland.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Optional;

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
        String content = event.getInvitor() + "님의 여행플랜에 초대합니다.";
        String url = BASE_FRONT_URL + "/planDetail/" + event.getPlanId();

        event.getInvitee().stream()
                .map(memberRepository::findByEmail)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(receiver -> notificationService.send(receiver, content, url, NotificationType.INVITE));
    }

    @TransactionalEventListener
    @Async
    public void handleDoAgree(DoEvent.DoAgreeEvent event) {
        String content = event.getInvitee() + "님이 여행플랜 초대를 수락하였습니다.";
        String url = BASE_FRONT_URL + "/planDetail/" + event.getPlanId();

        Plan plan = planRepository.findById(event.getPlanId()).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));
        notificationService.send(plan.getMember(), content, url, NotificationType.AGREE);
    }

    @TransactionalEventListener
    @Async
    public void handleDoDisagree(DoEvent.DoAgreeEvent event) {
        String content = event.getInvitee() + "님이 여행플랜 초대를 거절하였습니다.";
        String url = BASE_FRONT_URL + "/planDetail/" + event.getPlanId();

        Plan plan = planRepository.findById(event.getPlanId()).orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));
        notificationService.send(plan.getMember(), content, url, NotificationType.DISAGREE);
    }
}
