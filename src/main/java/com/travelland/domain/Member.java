package com.travelland.domain;

import com.travelland.constant.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long socialId;

    private String email;

    private String password;

    private String nickname;

    private String gender;

    private LocalDateTime birth;

    @Enumerated(EnumType.STRING)
    private Role role;
}
