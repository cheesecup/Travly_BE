package com.travelland.dto;

import lombok.AllArgsConstructor;
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
        private String profileImage;
        private String thumbnailProfileImage;

        @Builder
        public KakaoInfo(Long id, String nickname, String email, String name, String birth, String gender, String profileImage, String thumbnailProfileImage) {
            this.id = id;
            this.nickname = nickname;
            this.email = email;
            this.name = name;
            this.birth = birth;
            this.gender = gender;
            this.profileImage = profileImage;
            this.thumbnailProfileImage = thumbnailProfileImage;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private boolean isSuccess;
    }

    @Getter
    @AllArgsConstructor
    public static class DuplicateCheck {
        private boolean isAvailable;
    }

    @Getter
    public static class ChangeNicknameRequest {
        private String nickname;
    }
}