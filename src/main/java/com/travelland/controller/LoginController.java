//package com.travelland.controller;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.travelland.dto.member.MemberDto;
//import com.travelland.service.member.KakaoService;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatusCode;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.RestTemplate;
//
//@RestController
//@RequiredArgsConstructor
//public class LoginController {
//    private final KakaoService kakaoService;
//
//    @GetMapping(value = "/users/login/kakao")
//    public ResponseEntity kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
//        RestTemplate restTemplate = new RestTemplate();
//        MemberDto.MemberInfo memberInfo = kakaoService.kakaoLogin(code, response);
//
//        response.setHeader("Authorization", memberInfo.getCode());
//
//        return ResponseEntity.ok(memberInfo);
////        restTemplate.getForObject("https://www.travly.site", void.class, memberInfo);
//    }
//}
