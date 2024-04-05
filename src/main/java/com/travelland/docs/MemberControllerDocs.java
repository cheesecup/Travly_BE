package com.travelland.docs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.travelland.dto.MemberDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "회원 API", description = "회원 관련 API 명세서입니다.")
public interface MemberControllerDocs {
    @Operation(summary = "카카오 로그인", description = "카카오 로그인 API")
    ResponseEntity kakaoLogin(@RequestParam String code,
                              HttpServletResponse response) throws JsonProcessingException;

    @Operation(summary = "닉네임 변경", description = "닉네임 변경 API")
    ResponseEntity<MemberDto.Response> changeNickname(@RequestBody MemberDto.ChangeNicknameRequest request);

    @Operation(summary = "닉네임 중복체크", description = "닉네임 중복체크 API")
    ResponseEntity<MemberDto.DuplicateCheck> checkNickname(@PathVariable String nickname);
}
