package com.travelland.repository.plan;

import com.travelland.domain.plan.PlanVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanVoteRepository extends JpaRepository<PlanVote, Long> {
}
