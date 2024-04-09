package com.travelland.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.travelland.docs.MemberControllerDocs;
import com.travelland.dto.MemberDto;
import com.travelland.service.KakaoService;
import com.travelland.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class MemberController implements MemberControllerDocs {

    private final KakaoService kakaoService;
    private final MemberService memberService;

    @GetMapping("/login/kakao")
    public ResponseEntity<MemberDto.MemberInfo> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.OK).body(kakaoService.kakaoLogin(code, response));
    }

    @GetMapping("/logout")
    public ResponseEntity<MemberDto.Response> logout(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.status(HttpStatus.OK).body(new MemberDto.Response(memberService.logout(request, response)));
    }

    @DeleteMapping("/signout")
    public ResponseEntity<MemberDto.Response> signout(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.status(HttpStatus.OK).body(new MemberDto.Response(memberService.signout(request, response, "a@email.com")));
    }

    @PatchMapping
    public ResponseEntity<MemberDto.Response> changeNickname(@RequestBody MemberDto.ChangeNicknameRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(new MemberDto.Response(memberService.changeNickname(request, "a@email.com")));
    }

    @GetMapping("/{nickname}")
    public ResponseEntity<MemberDto.DuplicateCheck> checkNickname(@PathVariable String nickname) {
        return ResponseEntity.status(HttpStatus.OK).body(new MemberDto.DuplicateCheck(memberService.checkNickname(nickname)));
    }
}
