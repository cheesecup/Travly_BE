package com.travelland.repository.plan;

import com.travelland.domain.plan.PlanVote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanVoteRepository extends JpaRepository<PlanVote, Long> {
    Optional<PlanVote> findByIdAndIsDeleted(Long planVoteId, boolean isDeleted);

    Optional<PlanVote> findByIdAndIsDeletedAndIsClosed(Long planVoteId, boolean isDeleted, boolean isClosed);

    Page<PlanVote> findAllByIsDeleted(Pageable pageable, boolean isDeleted);
}
