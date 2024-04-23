package com.travelland.domain.member;

import com.travelland.constant.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberHistory {
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

    public MemberHistory(Member member) {
        this.email = member.getEmail();
        this.password = member.getPassword();
        this.nickname = member.getNickname();
        this.role = member.getRole();
        this.profileImage = member.getProfileImage();
    }
}
