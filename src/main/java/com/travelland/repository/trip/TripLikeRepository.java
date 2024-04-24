package com.travelland.repository.trip;

import com.travelland.domain.member.Member;
import com.travelland.domain.trip.Trip;
import com.travelland.domain.trip.TripLike;
import com.travelland.repository.trip.querydsl.CustomTripLikeRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TripLikeRepository extends JpaRepository<TripLike, Long>, CustomTripLikeRepository {

    Optional<TripLike> findByMemberAndTrip(Member member, Trip trip);

    Optional<TripLike> findByMemberAndTripAndIsDeleted(Member member, Trip trip, boolean isDeleted);

    boolean existsByMemberAndTripAndIsDeleted(Member member, Trip trip, boolean isDeleted);
}
