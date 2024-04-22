package com.travelland.service;

import com.travelland.constant.NotificationType;
import com.travelland.domain.member.Member;
import com.travelland.repository.member.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class NotificationServiceTest {
    @Autowired
    NotificationService notificationService;
    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("알림 구독을 진행한다.")
    public void subscribe() throws Exception {
        //given
        Member member = memberRepository.findById(1L).orElseThrow();
        String lastEventId = "";

        //when, then
        Assertions.assertDoesNotThrow(() -> notificationService.subscribe(member.getId(), lastEventId));
    }

    @Test
    @DisplayName("알림 메세지를 전송한다.")
    public void send() throws Exception {
        //given
        Member member = memberRepository.findById(1L).orElseThrow();
        String lastEventId = "";
        notificationService.subscribe(member.getId(), lastEventId);

        //when, then
        Assertions.assertDoesNotThrow(() -> notificationService.send(member, "코딩 스터디", "스터디 신청에 지원하셨습니다.", "localhost:8080/study/1", NotificationType.INVITE));
    }
}