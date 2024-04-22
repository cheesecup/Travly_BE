package com.travelland.controller;

import com.travelland.dto.member.MemberDto;
import com.travelland.global.security.UserDetailsImpl;
import com.travelland.service.member.MemberService;
import com.travelland.swagger.MemberControllerDocs;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class MemberController implements MemberControllerDocs {

    private final MemberService memberService;

    @GetMapping("/logout")
    public ResponseEntity<MemberDto.Response> logout(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.status(HttpStatus.OK).body(new MemberDto.Response(memberService.logout(request, response)));
    }

    @DeleteMapping("/signout")
    public ResponseEntity<MemberDto.Response> signout(HttpServletRequest request, HttpServletResponse response, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(new MemberDto.Response(memberService.signout(request, response, userDetails.getUsername())));
    }

    @PatchMapping("/change-nickname")
    public ResponseEntity<MemberDto.Response> changeNickname(@RequestBody MemberDto.ChangeNicknameRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(new MemberDto.Response(memberService.changeNickname(request, userDetails.getUsername())));
    }

    @GetMapping("/check-nickname/{nickname}")
    public ResponseEntity<MemberDto.DuplicateCheck> checkNickname(@PathVariable String nickname) {
        return ResponseEntity.status(HttpStatus.OK).body(new MemberDto.DuplicateCheck(memberService.checkNickname(nickname)));
    }

    @GetMapping("/search-nickname")
    public ResponseEntity<List<MemberDto.MemberInfo>> searchNickname(@RequestParam String nickname) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.searchNickname(nickname));
    }

    @GetMapping("/memberInfo")
    public ResponseEntity<MemberDto.MemberInfo> getMemberInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.getMemberInfo(userDetails.getMember()));
    }
}
