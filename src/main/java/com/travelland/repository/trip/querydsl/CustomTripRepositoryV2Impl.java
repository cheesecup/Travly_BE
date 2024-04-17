package com.travelland.repository.trip.querydsl;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import com.travelland.domain.member.Member;
import com.travelland.domain.trip.Trip;
import com.travelland.global.job.DataSet;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.travelland.domain.member.QMember.member;
import static com.travelland.domain.trip.QTrip.trip;
import static com.travelland.domain.trip.QTripHashtag.tripHashtag;

@Slf4j(topic = "Trip_queryDsl : ")
@Repository
@RequiredArgsConstructor
public class CustomTripRepositoryV2Impl implements CustomTripRepositoryV2 {

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager entityManager;

    @Override
    public List<Trip> getTripList(int page, int size, String sortBy, boolean isAsc) {
        OrderSpecifier orderSpecifier = createOrderSpecifier(sortBy, isAsc);

        return jpaQueryFactory.selectFrom(trip)
                .where(trip.isDeleted.eq(false))
                .orderBy(orderSpecifier, trip.id.desc())
                .limit(size)
                .offset((long) (page - 1) * size)
                .fetch();
    }

    @Override
    public List<Trip> getMyTripList(int page, int size, Member member) {
        return jpaQueryFactory.selectFrom(trip)
                .where(trip.isDeleted.eq(false), trip.member.eq(member))
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

//    @Override
//    public void updateBulkViewCount(List<DataSet> dataSets) {
//        log.info("update strat");
//        if (dataSets.isEmpty())
//            return;
//
//        CaseBuilder.Cases<Integer, NumberExpression<Integer>> cases = new CaseBuilder()
//                .when(trip.id.eq(dataSets.get(0).getId())).then(dataSets.get(0).getValue());
//
//        for (int i = 1 ; i < dataSets.size() ; i++)
//            cases = cases.when(trip.id.eq(dataSets.get(i).getId())).then(dataSets.get(i).getValue());
//
//        new JPAUpdateClause(entityManager, trip)
//                .set(trip.viewCount, cases.otherwise(trip.viewCount))
//                .where(trip.id.in(dataSets.stream().map(DataSet::getId).toList()))
//                .execute();
//    }

    @Override
    public void updateBulkViewCount(List<DataSet> dataSets) {
        log.info("update strat");
//        if (dataSets.isEmpty())
//            return;
//
//        CaseBuilder.Cases<Integer, NumberExpression<Integer>> cases = new CaseBuilder()
//                .when(trip.id.eq(dataSets.get(0).getId())).then(dataSets.get(0).getValue());
//
//        for (int i = 1 ; i < dataSets.size() ; i++)
//            cases = cases.when(trip.id.eq(dataSets.get(i).getId())).then(dataSets.get(i).getValue());
//
//        new JPAUpdateClause(entityManager, trip)
//                .set(trip.viewCount, cases.otherwise(trip.viewCount))
//                .where(trip.id.in(dataSets.stream().map(DataSet::getId).toList()))
//                .execute();
    }

    @Override
    public Optional<Trip> getTripById(Long tripId) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(trip)
                .leftJoin(trip.member, member).fetchJoin()
                .where(trip.id.eq(tripId))
                .fetchOne());
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
