package com.travelland.repository.trip;

import com.travelland.domain.trip.Trip;
import com.travelland.repository.trip.querydsl.CustomTripRepositoryV2;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long>, CustomTripRepositoryV2 {

    Optional<Trip> findByIdAndIsDeletedAndIsPublic(Long tripId, boolean isDeleted, boolean isPublic);

    Optional<Trip> findByIdAndIsDeleted(Long tripId, boolean isDeleted);


}
