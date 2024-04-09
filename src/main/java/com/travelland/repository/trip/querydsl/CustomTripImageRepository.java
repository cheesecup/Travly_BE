package com.travelland.repository.trip.querydsl;

import com.travelland.domain.trip.Trip;

public interface CustomTripImageRepository {

    long deleteByTrip(Trip trip);
}
