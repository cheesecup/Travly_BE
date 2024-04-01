package com.travelland.repository;

import com.travelland.domain.Trip;
import com.travelland.domain.TripHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripHashtagRepository extends JpaRepository<TripHashtag, Long> {

    List<TripHashtag> findAllByTrip(Trip trip);

    void deleteByTrip(Trip trip);
}
