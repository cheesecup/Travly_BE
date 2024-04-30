package com.travelland.controller;

import com.travelland.global.security.UserDetailsImpl;
import com.travelland.service.notification.NotificationService;
import com.travelland.swagger.NotificationControllerDocs;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class NotificationController implements NotificationControllerDocs {
    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(notificationService.subscribe(userDetails.getMember().getId()));
    }

    @GetMapping("/subscribe/past-data")
    public ResponseEntity<?> sendPastData(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        notificationService.sendPastData(userDetails.getMember().getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}