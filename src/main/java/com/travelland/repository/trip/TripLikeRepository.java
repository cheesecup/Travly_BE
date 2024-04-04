package com.travelland.repository.trip;

import com.travelland.domain.member.Member;
import com.travelland.domain.Trip;
import com.travelland.domain.TripLike;
import com.travelland.repository.trip.querydsl.CustomTripLikeRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TripLikeRepository extends JpaRepository<TripLike, Long>, CustomTripLikeRepository {

    void deleteAllByTrip(Trip trip);

    Optional<TripLike> findByMemberAndTrip(Member member, Trip trip);

    List<TripLike> getLikeListByMember(Member member, int size, int page);
}
