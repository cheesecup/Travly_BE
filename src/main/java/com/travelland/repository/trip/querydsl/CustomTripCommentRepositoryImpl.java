package com.travelland.repository.trip.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.travelland.domain.trip.QTripComment;
import com.travelland.domain.trip.Trip;
import com.travelland.domain.trip.TripComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.travelland.domain.trip.QTripComment.*;

@Repository
@RequiredArgsConstructor
public class CustomTripCommentRepositoryImpl implements CustomTripCommentRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<TripComment> getTripCommentList(Trip trip, int page, int size) {
        return jpaQueryFactory.selectFrom(tripComment)
                .where(tripComment.trip.eq(trip), tripComment.isDeleted.eq(false))
                .orderBy(tripComment.createdAt.desc())
                .limit(size)
                .offset((long) (page - 1) * size)
                .fetch();
    }
}
