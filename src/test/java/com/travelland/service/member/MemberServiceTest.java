package com.travelland.service.member;

import com.travelland.domain.member.Member;
import com.travelland.repository.member.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class MemberServiceTest {
    @Autowired
    MemberRepository memberRepository;

//    @Test
//    void createMember() {
//        for(int i = 0 ; i < 10000 ; i++){
//            memberRepository.save(Member.builder().email("a10"+i).build());
//        }
//    }
}