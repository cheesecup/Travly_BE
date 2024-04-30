package com.travelland.repository.trip.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.travelland.domain.trip.Trip;
import com.travelland.domain.trip.TripComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.travelland.domain.trip.QTripComment.tripComment;

@Repository
@RequiredArgsConstructor
public class CustomTripCommentRepositoryImpl implements CustomTripCommentRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 여행후기에 등록된 댓글 목록 조회
     * @param trip 조회할 여행후기 id
     * @param page 조회할 페이지 번호
     * @param size 한 페이지에 보여지는 댓글 수
     * @return 조회된 댓글 목록
     */
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
