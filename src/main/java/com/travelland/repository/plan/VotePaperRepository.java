package com.travelland.repository.plan;

import com.travelland.domain.plan.VotePaper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VotePaperRepository extends JpaRepository<VotePaper, Long> {
    Optional<VotePaper> findByIdAndIsDeleted(Long votePaperId, boolean isDeleted);

    Page<VotePaper> findAllByIsDeletedAndMemberId(Pageable pageable, boolean isDeleted, Long memberId);
    Page<VotePaper> findAllByIsDeleted(Pageable pageable, boolean isDeleted);
}
