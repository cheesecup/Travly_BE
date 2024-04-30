package com.travelland.repository.trip;

import com.travelland.domain.trip.TripComment;
import com.travelland.repository.trip.querydsl.CustomTripCommentRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripCommentRepository extends JpaRepository<TripComment, Long>, CustomTripCommentRepository {
}
