package com.travelland.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.travelland.dto.MemberDto;
import com.travelland.global.jwt.JwtUtil;
import com.travelland.service.KakaoService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class MemberController {

    private final KakaoService kakaoService;

    @GetMapping("/login/kakao")
    public ResponseEntity<MemberDto.Response> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        String token = kakaoService.kakaoLogin(code);

        // Barer 조심!!!!!
        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, token.substring(7));
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResponseEntity.status(HttpStatus.OK).body(new MemberDto.Response(true));
    }
}
