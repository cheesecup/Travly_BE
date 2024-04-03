package com.travelland.repository.trip.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.travelland.domain.Member;
import com.travelland.domain.TripScrap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.travelland.domain.QTripScrap.tripScrap;

@Repository
@RequiredArgsConstructor
public class CustomTripScrapRepositoryImpl implements CustomTripScrapRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<TripScrap> getScrapListByMember(Member member, int size, int page) {
        return jpaQueryFactory.selectFrom(tripScrap)
                .where(tripScrap.member.eq(member), tripScrap.isDeleted.eq(false))
                .orderBy(tripScrap.trip.createdAt.desc())
                .limit(size)
                .offset((long) (page - 1) * size)
                .fetch();
    }
}
