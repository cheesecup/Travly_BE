package com.travelland.repository;

import com.travelland.domain.Member;
import com.travelland.domain.Trip;
import com.travelland.domain.TripLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TripLikeRepository extends JpaRepository<TripLike, Long> {

    void deleteAllByTrip(Trip trip);

    Optional<TripLike> findByMemberAndTrip(Member member, Trip trip);
}
