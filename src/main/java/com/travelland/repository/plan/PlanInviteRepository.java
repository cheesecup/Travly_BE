package com.travelland.repository.plan;

import com.travelland.domain.member.Member;
import com.travelland.domain.plan.PlanInvite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanInviteRepository extends JpaRepository<PlanInvite, Long> {

    Optional<PlanInvite> findByPlanIdAndMemberEmail(Long planId, String email);
    Optional<PlanInvite> findByPlanIdAndMember(Long planId, Member member);
    Page<PlanInvite> findByMemberNickname(Pageable pageable, String nickname);

}
