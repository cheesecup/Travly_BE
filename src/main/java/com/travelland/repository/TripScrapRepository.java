package com.travelland.repository;

import com.travelland.domain.Member;
import com.travelland.domain.Trip;
import com.travelland.domain.TripScrap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripScrapRepository extends JpaRepository<TripScrap, Long> {

    void deleteByMemberAndTrip(Member member, Trip trip);

    void deleteAllByTrip(Trip trip);
}
