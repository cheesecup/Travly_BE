package com.travelland.docs;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "로그인 API", description = "로그인 관련 API 명세서입니다.")
public interface MemberControllerDocs {
    @Operation(summary = "카카오 로그인", description = "카카오 로그인 API")
    ResponseEntity kakaoLogin(@RequestParam String code,
                              HttpServletResponse response) throws JsonProcessingException;
}
