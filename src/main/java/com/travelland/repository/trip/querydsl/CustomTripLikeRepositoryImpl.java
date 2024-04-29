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

    /**
     * 회원이 등록한 좋아요 목록 조회
     * @param member 조회 대상 회원정보
     * @param size 한 페이지에 보여지는 좋아요 목록 수
     * @param page 조회할 페이지 번호
     * @return 조회된 좋아요 목록
     */
    @Override
    public List<TripLike> getLikeListByMember(Member member, int size, int page) {
        return jpaQueryFactory.selectFrom(tripLike)
                .where(tripLike.member.eq(member), tripLike.isDeleted.eq(false), tripLike.trip.isDeleted.eq(false))
                .orderBy(tripLike.trip.createdAt.desc())
                .limit(size)
                .offset((long) (page - 1) * size)
                .fetch();
    }

    /**
     * 여행후기에 좋아요 등록한 회원 식별 id 목록 조회
     * @param tripId 여행후기 id
     * @param size 한 페이지에 보여지는 목록 수
     * @param page 조회할 페이지 번호
     * @return 조회된 회원 식별 id
     */
    @Override
    public List<Long> getMemberIdsByTripId(Long tripId, int size, int page) {
        return  jpaQueryFactory.select(tripLike.member.id)
                .from(tripLike)
                .where(tripLike.trip.id.eq(tripId))
                .limit(size)
                .offset((long) (page - 1) * size)
                .fetch();
    }

    /**
     * 회원이 좋아요한 여행후기 id 목록 조회
     * @param memberId 회원 식별 id
     * @param size 한 페이지에 보여지는 목록 수
     * @param page 조회할 페이지 번호
     * @return 조회된 여행후기 id
     */
    @Override
    public List<Long> getTripIdsByMemberId(Long memberId, int size, int page) {
        return  jpaQueryFactory.select(tripLike.trip.id)
                .from(tripLike)
                .where(tripLike.member.id.eq(memberId))
                .limit(size)
                .offset((long) (page - 1) * size)
                .fetch();
    }

    /**
     * 여행후기와 관련된 좋아요 삭제
     * @param trip 여행후기 id
     * @return 삭제된 좋아요 개수
     */
    @Override
    public long deleteByTrip(Trip trip) {
        return jpaQueryFactory.update(tripLike)
                .set(tripLike.isDeleted, true)
                .where(tripLike.trip.eq(trip))
                .execute();
    }
}
