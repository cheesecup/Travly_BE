package com.travelland.repository.trip;

import com.travelland.domain.trip.Trip;
import com.travelland.domain.trip.TripHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripHashtagRepository extends JpaRepository<TripHashtag, Long> {

    List<TripHashtag> findAllByTrip(Trip trip);

    void deleteByTrip(Trip trip);

    long countByTitle(String title);
}
