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

    /**
     * 회원이 등록한 스크랩 목록 조회
     * @param member 조회 대상 회원정보
     * @param size 한 페이지에 보여지는 스크랩 목록 수
     * @param page 조회할 페이지 번호
     * @return 조회된 스크랩 목록
     */
    @Override
    public List<TripScrap> getScrapListByMember(Member member, int size, int page) {
        return jpaQueryFactory.selectFrom(tripScrap)
                .join(tripScrap.trip)
                .where(tripScrap.member.eq(member), tripScrap.isDeleted.eq(false), tripScrap.trip.isDeleted.eq(false))
                .orderBy(tripScrap.trip.createdAt.desc())
                .limit(size)
                .offset((long) (page - 1) * size)
                .fetch();
    }

    /**
     * 여행후기와 관련된 스크랩 삭제
     * @param trip 여행후기 id
     * @return 삭제된 스크랩 개수
     */
    @Override
    public long deleteByTrip(Trip trip) {
        return jpaQueryFactory.update(tripScrap)
                .set(tripScrap.isDeleted, true)
                .where(tripScrap.trip.eq(trip))
                .execute();
    }
}
