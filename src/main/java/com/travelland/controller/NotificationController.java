package com.travelland.controller;

import com.travelland.global.security.UserDetailsImpl;
import com.travelland.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(notificationService.subscribe(userDetails.getMember().getId()));
    }

    @GetMapping("/subscribe/lost-data")
    public ResponseEntity<?> sendLostData(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                          @RequestParam String lastEventId) {
        notificationService.sendLostData(lastEventId, userDetails.getMember().getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}