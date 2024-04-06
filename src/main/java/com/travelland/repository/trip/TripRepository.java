package com.travelland.repository.trip;

import com.travelland.domain.trip.Trip;
import com.travelland.repository.trip.querydsl.CustomTripRepositoryV2;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Long>, CustomTripRepositoryV2 {
}
