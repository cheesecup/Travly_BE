package com.travelland.repository;

import com.travelland.domain.Trip;
import com.travelland.domain.TripImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TripImageRepository extends JpaRepository<TripImage, Long> {

    List<TripImage> findAllByTrip(Trip trip);

    Optional<TripImage> findByTripAndIsThumbnail(Trip trip, boolean isThumbnail);

    void deleteByTrip(Trip trip);
}
