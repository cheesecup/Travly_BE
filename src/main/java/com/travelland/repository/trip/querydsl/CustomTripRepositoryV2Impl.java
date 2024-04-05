package com.travelland.repository.trip.querydsl;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.travelland.domain.Member;
import com.travelland.domain.trip.Trip;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.travelland.domain.trip.QTrip.trip;
import static com.travelland.domain.trip.QTripHashtag.tripHashtag;

@Repository
@RequiredArgsConstructor
public class CustomTripRepositoryV2Impl implements CustomTripRepositoryV2 {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Trip> getTripList(int page, int size, String sortBy, boolean isAsc) {
        OrderSpecifier orderSpecifier = createOrderSpecifier(sortBy, isAsc);

        return jpaQueryFactory.selectFrom(trip)
                .orderBy(orderSpecifier, trip.id.desc())
                .limit(size)
                .offset((long) (page - 1) * size)
                .fetch();
    }

    @Override
    public List<Trip> getMyTripList(int page, int size, Member member) {
        return jpaQueryFactory.selectFrom(trip)
                .where(trip.member.eq(member))
                .orderBy(trip.createdAt.desc())
                .limit(size)
                .offset((long) (page - 1) * size)
                .fetch();
    }

    @Override
    public List<Trip> searchTripByHashtag(String hashtag, int page, int size, String sortBy, boolean isAsc) {
        OrderSpecifier orderSpecifier = createOrderSpecifier(sortBy, isAsc);

        return jpaQueryFactory.select(trip)
                .from(tripHashtag)
                .rightJoin(trip).on(tripHashtag.trip.id.eq(trip.id))
                .where(tripHashtag.title.eq(hashtag))
                .orderBy(orderSpecifier, trip.id.desc())
                .limit(size)
                .offset((long) (page - 1) * size)
                .fetch();
    }

    private OrderSpecifier createOrderSpecifier(String sortBy, boolean isAsc) {
        Order order = (isAsc) ? Order.ASC : Order.DESC;

        return switch (sortBy) {
            case "viewCount" -> new OrderSpecifier<>(order, trip.viewCount);
            case "title" -> new OrderSpecifier<>(order, trip.title);
            default -> new OrderSpecifier<>(order, trip.createdAt);
        };
    }
}
