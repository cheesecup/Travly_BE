package com.travelland.domain.member;

import com.travelland.global.support.BeanUtils;
import com.travelland.repository.member.MemberHistoryRepository;
import jakarta.persistence.PreRemove;

public class MemberEntityListener {

    @PreRemove
    public void preDelete(Member member){
        MemberHistoryRepository memberHistoryRepository
                = BeanUtils.getBean(MemberHistoryRepository.class);
        MemberHistory memberHistory = new MemberHistory(member);
        memberHistoryRepository.save(memberHistory);
    }
}
