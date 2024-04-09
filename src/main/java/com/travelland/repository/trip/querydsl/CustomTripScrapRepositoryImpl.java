package com.travelland.repository.trip.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.travelland.domain.member.Member;
import com.travelland.domain.trip.Trip;
import com.travelland.domain.trip.TripScrap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.travelland.domain.trip.QTripScrap.tripScrap;

@Repository
@RequiredArgsConstructor
public class CustomTripScrapRepositoryImpl implements CustomTripScrapRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<TripScrap> getScrapListByMember(Member member, int size, int page) {
        return jpaQueryFactory.selectFrom(tripScrap)
                .where(tripScrap.member.eq(member), tripScrap.isDeleted.eq(false), tripScrap.trip.isDeleted.eq(false))
                .orderBy(tripScrap.trip.createdAt.desc())
                .limit(size)
                .offset((long) (page - 1) * size)
                .fetch();
    }

    @Override
    public long deleteByTrip(Trip trip) {
        return jpaQueryFactory.update(tripScrap)
                .set(tripScrap.isDeleted, true)
                .where(tripScrap.trip.eq(trip))
                .execute();
    }
}
