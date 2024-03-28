package com.travelland.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

public class MemberRequestDto {

    @Getter
    @ToString
    @AllArgsConstructor
    public static class LoginRequestDto {
        private String email;
        private String password;
    }
}
