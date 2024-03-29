package com.travelland.controller;

import com.travelland.docs.TestControllerDocs;
import com.travelland.dto.MemberRequestDto;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController implements TestControllerDocs {

    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok("TEST1");
    }

    @GetMapping("/swagger-test")
    public ResponseEntity<String> swaggerTest(MemberRequestDto.LoginRequestDto requestDto) {
        return ResponseEntity.ok("Swagger Test Success!");
    }

    @PostMapping("/posts")
    public ResponseEntity postRequest(@RequestParam String name) {
        return ResponseEntity.status(HttpStatus.CREATED).body(name);
    }

    @PostMapping("/errors")
    public ResponseEntity errorResponseTest(@RequestParam String isError) {
        if(isError.equalsIgnoreCase("true"))
                throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        return ResponseEntity.status(HttpStatus.CREATED).body("false");
    }
}
