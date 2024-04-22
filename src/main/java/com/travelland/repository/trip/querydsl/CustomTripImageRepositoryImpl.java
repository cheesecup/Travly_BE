package com.travelland.repository.trip.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.travelland.domain.trip.QTripImage;
import com.travelland.domain.trip.Trip;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.travelland.domain.trip.QTripImage.tripImage;

@Repository
@RequiredArgsConstructor
public class CustomTripImageRepositoryImpl implements CustomTripImageRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public long deleteByTrip(Trip trip) {
        return jpaQueryFactory.update(tripImage)
                .set(tripImage.isDeleted, true)
                .where(tripImage.trip.eq(trip))
                .execute();
    }
}
