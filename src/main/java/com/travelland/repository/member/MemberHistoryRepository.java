package com.travelland.repository.member;

import com.travelland.domain.member.MemberHistory;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MemberHistoryRepository extends JpaRepository<MemberHistory, Long> {
}
