package com.travelland.dto;

import lombok.Builder;
import lombok.Getter;

public class MemberDto {

    @Getter
    public static class KakaoInfo {
        private Long id;
        private String nickname;
        private String email;
        private String name;
        private String birth;
        private String gender;

        @Builder
        public KakaoInfo(Long id, String nickname, String email, String name, String birth, String gender) {
            this.id = id;
            this.nickname = nickname;
            this.email = email;
            this.name = name;
            this.birth = birth;
            this.gender = gender;
        }
    }
}