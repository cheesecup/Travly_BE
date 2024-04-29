package com.travelland.swagger;

import com.travelland.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "알림 API", description = "알림 연결을 위한 API")
public interface NotificationControllerDocs {

    @Operation(summary = "알림 구독", description = "알림 연결하는 API. 로그인 시 요청 필수.")
    ResponseEntity subscribe(@AuthenticationPrincipal UserDetailsImpl userDetails);

    @Operation(summary = "놓친 알림 수신", description = "알림 연결 해제되어있을 때 온 알림 수신하는 API. lastEventId에 아무것도 입력하지 않으면 수신했던 모든 알림 수신.")
    ResponseEntity sendLostData(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam String lastEventId);

}
