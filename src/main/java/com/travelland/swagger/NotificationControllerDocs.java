package com.travelland.swagger;

import com.travelland.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Tag(name = "알림 API", description = "알림 연결을 위한 API")
public interface NotificationControllerDocs {

    @Operation(summary = "알림 구독", description = "알림 연결하는 API. 로그인 시 요청 필수.")
    ResponseEntity subscribe(@AuthenticationPrincipal UserDetailsImpl userDetails);

    @Operation(summary = "지난 알림 수신", description = "읽지 않은 지난 알림 수신하는 API. Plan 초대 수락, 거절 메시지 제외.")
    ResponseEntity sendPastData(@AuthenticationPrincipal UserDetailsImpl userDetails);

}
