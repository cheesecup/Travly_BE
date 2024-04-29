package com.travelland.repository.trip;

import com.travelland.domain.member.Member;
import com.travelland.domain.trip.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {

    Optional<Trip> findByIdAndIsDeleted(Long tripId, boolean isDeleted);

    List<Trip> findAllByIsDeleted(boolean isDeleted);

    long countByMemberAndIsDeleted(Member member, boolean isDeleted);
}
