package com.travelland.repository;

import com.travelland.domain.Member;
import com.travelland.domain.Trip;
import com.travelland.domain.TripScrap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TripScrapRepository extends JpaRepository<TripScrap, Long> {

    void deleteAllByTrip(Trip trip);

    Optional<TripScrap> findByMemberAndTrip(Member member, Trip trip);
}
