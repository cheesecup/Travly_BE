package com.travelland.repository.trip.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.travelland.domain.Member;
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
                .where(tripLike.member.eq(member), tripLike.isDeleted.eq(false))
                .orderBy(tripLike.trip.createdAt.desc())
                .limit(size)
                .offset((long) (page - 1) * size)
                .fetch();
    }
}
