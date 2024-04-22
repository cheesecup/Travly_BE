package com.travelland.repository.trip.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.travelland.domain.member.Member;
import com.travelland.domain.trip.Trip;
import com.travelland.domain.trip.TripLike;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.travelland.domain.trip.QTripLike.tripLike;

@Repository
@RequiredArgsConstructor
public class CustomTripLikeRepositoryImpl implements CustomTripLikeRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<TripLike> getLikeListByMember(Member member, int size, int page) {
        return jpaQueryFactory.selectFrom(tripLike)
                .where(tripLike.member.eq(member), tripLike.isDeleted.eq(false), tripLike.trip.isDeleted.eq(false))
                .orderBy(tripLike.trip.createdAt.desc())
                .limit(size)
                .offset((long) (page - 1) * size)
                .fetch();
    }

    @Override
    public List<Long> getMemberIdsByTripId(Long tripId, int size, int page) {
        return  jpaQueryFactory.select(tripLike.member.id)
                .from(tripLike)
                .where(tripLike.trip.id.eq(tripId))
                .limit(size)
                .offset((long) (page - 1) * size)
                .fetch();
    }

    @Override
    public List<Long> getTripIdsByMemberId(Long memberId, int size, int page) {
        return  jpaQueryFactory.select(tripLike.trip.id)
                .from(tripLike)
                .where(tripLike.member.id.eq(memberId))
                .limit(size)
                .offset((long) (page - 1) * size)
                .fetch();
    }

    @Override
    public long deleteByTrip(Trip trip) {
        return jpaQueryFactory.update(tripLike)
                .set(tripLike.isDeleted, true)
                .where(tripLike.trip.eq(trip))
                .execute();
    }
}
