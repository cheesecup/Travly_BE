package com.travelland.repository.plan;

import com.travelland.domain.plan.VotePaper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VotePaperRepository extends JpaRepository<VotePaper, Long> {
    Page<VotePaper> findAllByMemberId(Pageable pageable, Long memberId);
}
