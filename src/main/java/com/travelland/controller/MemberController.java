package com.travelland.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelland.dto.member.MemberDto;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.global.security.UserDetailsImpl;
import com.travelland.service.member.KakaoService;
import com.travelland.service.member.MemberService;
import com.travelland.swagger.MemberControllerDocs;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class MemberController implements MemberControllerDocs {

    private final KakaoService kakaoService;
    private final MemberService memberService;

//    @GetMapping("/login/kakao")
//    public ResponseEntity<MemberDto.MemberInfo> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
//        MemberDto.MemberInfo memberInfo = kakaoService.kakaoLogin(code, response);

//        response.setContentType("application/json");
//        response.setCharacterEncoding("utf-8");

//        ObjectMapper objectMapper = new ObjectMapper();
//        String result = objectMapper.writeValueAsString(memberInfo);
//        return ResponseEntity.status(HttpStatus.OK).body(memberInfo);
//        try {
//            response.getWriter().write(result);
//            response.sendRedirect("https://www.travly.site");
//        } catch (IOException e) {
//            throw new CustomException(ErrorCode.SERVER_ERROR);
//        }
//    }

    @GetMapping("/logout")
    public ResponseEntity<MemberDto.Response> logout(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.status(HttpStatus.OK).body(new MemberDto.Response(memberService.logout(request, response)));
    }

    @DeleteMapping("/signout")
    public ResponseEntity<MemberDto.Response> signout(HttpServletRequest request, HttpServletResponse response, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(new MemberDto.Response(memberService.signout(request, response, userDetails.getUsername())));
    }

    @PatchMapping
    public ResponseEntity<MemberDto.Response> changeNickname(@RequestBody MemberDto.ChangeNicknameRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(new MemberDto.Response(memberService.changeNickname(request, userDetails.getUsername())));
    }

    @GetMapping("/{nickname}")
    public ResponseEntity<MemberDto.DuplicateCheck> checkNickname(@PathVariable String nickname) {
        return ResponseEntity.status(HttpStatus.OK).body(new MemberDto.DuplicateCheck(memberService.checkNickname(nickname)));
    }
}
