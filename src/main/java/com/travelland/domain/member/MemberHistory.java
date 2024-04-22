package com.travelland.domain.member;

import com.travelland.constant.Gender;
import com.travelland.constant.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long socialId;

    @Column(length = 30)
    private String email;

    private String password;

    @Column(length = 8)
    private String name;

    @Column(length = 15)
    private String nickname;

    @Column(length = 8)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate birth;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private Role role;

    public MemberHistory(Member member) {
        this.socialId = member.getSocialId();
        this.email = member.getEmail();
        this.password = member.getPassword();
        this.name = member.getName();
        this.nickname = member.getNickname();
        this.gender = member.getGender();
        this.birth = member.getBirth();
        this.role = member.getRole();
    }
}
