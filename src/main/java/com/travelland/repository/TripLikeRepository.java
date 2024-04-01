package com.travelland.repository;

import com.travelland.domain.Member;
import com.travelland.domain.Trip;
import com.travelland.domain.TripLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripLikeRepository extends JpaRepository<TripLike, Long> {

    void deleteByMemberAndTrip(Member member, Trip trip);

    void deleteAllByTrip(Trip trip);
}
