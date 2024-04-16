package com.travelland.repository.trip;

import com.travelland.domain.trip.Trip;
import com.travelland.repository.trip.querydsl.CustomTripRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long>, CustomTripRepository {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Trip> findByIdAndIsDeletedAndIsPublic(Long tripId, boolean isDeleted, boolean isPublic);

    Optional<Trip> findByIdAndIsDeleted(Long tripId, boolean isDeleted);
}
