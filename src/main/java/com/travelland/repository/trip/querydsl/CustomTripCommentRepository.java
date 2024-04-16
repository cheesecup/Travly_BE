package com.travelland.repository.trip.querydsl;

import com.travelland.domain.trip.Trip;
import com.travelland.domain.trip.TripComment;

import java.util.List;

public interface CustomTripCommentRepository {

    List<TripComment> getTripCommentList(Trip trip, int page, int size);
}
