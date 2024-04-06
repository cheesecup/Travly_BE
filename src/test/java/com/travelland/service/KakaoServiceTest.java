//package com.travelland.service;
//
//import com.travelland.dto.MemberDto;
//import net.datafaker.Faker;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
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