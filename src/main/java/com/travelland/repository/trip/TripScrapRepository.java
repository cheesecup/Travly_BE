package com.travelland.repository.trip;

import com.travelland.domain.member.Member;
import com.travelland.domain.Trip;
import com.travelland.domain.TripScrap;
import com.travelland.repository.trip.querydsl.CustomTripScrapRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TripScrapRepository extends JpaRepository<TripScrap, Long>, CustomTripScrapRepository {

    void deleteAllByTrip(Trip trip);

    Optional<TripScrap> findByMemberAndTrip(Member member, Trip trip);

    List<TripScrap> getScrapListByMember(Member member, int size, int page);
}
