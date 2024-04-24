package com.travelland.repository.trip;

import com.travelland.domain.trip.Trip;
import com.travelland.domain.trip.TripImage;
import com.travelland.repository.trip.querydsl.CustomTripImageRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TripImageRepository extends JpaRepository<TripImage, Long>, CustomTripImageRepository {

    List<TripImage> findAllByTrip(Trip trip);

    Optional<TripImage> findByTripAndIsThumbnail(Trip trip, boolean isThumbnail);

    List<TripImage> findAllByTripAndIsThumbnail(Trip trip, boolean b);
}
