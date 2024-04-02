package com.travelland.domain;

import com.travelland.constant.Gender;
import com.travelland.constant.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long socialId;

    @Column(length = 30)
    private String email;

    private String password;

    @Column(length = 15)
    private String nickname;

    @Column(length = 8)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate birth;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public Member(Long socialId, String email, String password, String nickname, Gender gender, LocalDate birth, Role role) {
        this.socialId = socialId;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.gender = gender;
        this.birth = birth;
        this.role = role;
    }
}
