package com.travelland.dto.member;

import com.travelland.constant.Gender;
import com.travelland.constant.Role;
import com.travelland.domain.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Map;

@Getter
@ToString
public class OAuthAttributes {
    private Map<String, Object> attributes;     // OAuth2 반환하는 유저 정보
    private String nameAttributesKey;
    private String name;
    private String nickname;
    private String email;
    private String gender;
    private String birthyear;
    private String birthday;
    private String profileImageUrl;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributesKey, String name, String nickname, String email, String gender, String birthday, String birthyear, String profileImageUrl) {
        this.attributes = attributes;
        this.nameAttributesKey = nameAttributesKey;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.gender = gender;
        this.birthday = birthday;
        this.birthyear = birthyear;
        this.profileImageUrl = profileImageUrl;
    }

    public static OAuthAttributes of(String socialName, Map<String, Object> attributes) {
        if ("kakao".equals(socialName)) {
            return ofKakao("id", attributes);
        }

        return null;
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuthAttributes.builder()
                .name(String.valueOf(kakaoAccount.get("name")))
                .nickname(String.valueOf(kakaoProfile.get("nickname")))
                .email(String.valueOf(kakaoAccount.get("email")))
                .gender(String.valueOf(kakaoAccount.get("gender")))
                .birthyear(String.valueOf(kakaoAccount.get("birthyear")))
                .birthday(String.valueOf(kakaoAccount.get("birthday")))
                .profileImageUrl(String.valueOf(kakaoProfile.get("thumbnail_image_url")))
                .nameAttributesKey(userNameAttributeName)
                .attributes(attributes)
                .build();
    }

    public Member toEntity() {
        return Member.builder()
                .name(name)
                .nickname(nickname)
                .email(email)
                .gender(Gender.valueOf(gender.toUpperCase()))
                .role(Role.USER)
                .birth(LocalDate.of(Integer.parseInt(birthyear), Integer.parseInt(birthday.substring(0,2)), Integer.parseInt(birthday.substring(2,4))))
                .profileImage(profileImageUrl)
                .build();
    }
}