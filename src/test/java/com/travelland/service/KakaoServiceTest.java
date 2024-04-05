//package com.travelland.service;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.travelland.dto.MemberDto;
//import jakarta.servlet.http.HttpServletResponse;
//import net.datafaker.Faker;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//@SpringBootTest
//class KakaoServiceTest {
//    @Autowired
//    private KakaoService kakaoService;
//
//
//    @Test
//void joinMember() {
//        Faker faker = new Faker();
//
//        MemberDto.KakaoInfo user = MemberDto.KakaoInfo.builder()
//                .id(faker.number().randomNumber())
//                .nickname(faker.name().username())
//                .email("test@test.com")
//                .name(faker.name().fullName())
//                .birth("19800123")
//                .gender(faker.options().option("male", "female"))
//                .build();
//
//        kakaoService.registerKakaoUserIfNeeded(user);
//
//  }
//}