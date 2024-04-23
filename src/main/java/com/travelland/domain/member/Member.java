package com.travelland.domain.member;

import com.travelland.constant.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(MemberEntityListener.class)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30)
    private String email;

    private String password;

    @Column(length = 15)
    private String nickname;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private Role role;

    private String profileImage;

    @Builder
    public Member(String email, String password, String nickname, Role role, String profileImage) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
        this.profileImage = profileImage;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public Member changeProfileImage(String profileImage) {
        this.profileImage = profileImage;
        return this;
    }
}
