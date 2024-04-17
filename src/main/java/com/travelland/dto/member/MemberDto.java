package com.travelland.dto.member;

import com.travelland.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class MemberDto {

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

    @Getter
    public static class MemberInfo {
        private String nickname;
        private String email;
        private String profileImage;

        public MemberInfo(Member member) {
            this.nickname = member.getNickname();
            this.email = member.getEmail();
            this.profileImage = member.getProfileImage();
        }
    }
}