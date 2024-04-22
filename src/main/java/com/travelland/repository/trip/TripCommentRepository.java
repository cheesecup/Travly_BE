package com.travelland.repository.trip;

import com.travelland.domain.trip.TripComment;
import com.travelland.repository.trip.querydsl.CustomTripCommentRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripCommentRepository extends JpaRepository<TripComment, Long>, CustomTripCommentRepository {

    List<TripComment> findAllByTripId(Long tripId);
}
