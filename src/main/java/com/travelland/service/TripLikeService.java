package com.travelland.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.travelland.domain.Member;
import com.travelland.domain.QTripLike;
import com.travelland.domain.Trip;
import com.travelland.domain.TripLike;
import com.travelland.dto.TripDto;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.repository.MemberRepository;
import com.travelland.repository.TripLikeRepository;
import com.travelland.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripLikeService {

    private final TripLikeRepository tripLikeRepository;
    private final MemberRepository memberRepository;
    private final TripRepository tripRepository;
    private final JPAQueryFactory jpaQueryFactory;

    //여행정보 좋아요 등록
    @Transactional
    public void createTripLike(Long tripId, String email) {
        Member member = getMember(email);
        Trip trip = getTrip(tripId);

        tripLikeRepository.save(new TripLike(member, trip));
        trip.increaseLikeCount();
    }

    //여행정보 좋아요 취소
    @Transactional
    public void deleteTripLike(Long tripId, String email) {
        Member member = getMember(email);
        Trip trip = getTrip(tripId);

        tripLikeRepository.deleteByMemberAndTrip(member, trip);
        trip.decreaseLikeCount();
    }

    //여행정보 좋아요 목록 조회
    @Transactional(readOnly = true)
    public List<TripDto.GetTripLikeListResponse> getTripLikeList(int page, int size, String sort, boolean ASC, String email) {
        Member member = getMember(email);

        List<TripLike> tripLikeList = jpaQueryFactory.selectFrom(QTripLike.tripLike)
                .where(QTripLike.tripLike.member.eq(member))
                .orderBy(QTripLike.tripLike.trip.createdAt.desc())
                .limit(size)
                .offset((long) (page - 1) * size)
                .fetch();

        return tripLikeList.stream().map(tripLike -> new TripDto.GetTripLikeListResponse(tripLike.getTrip(), tripLike.getMember()))
                .collect(Collectors.toList());
    }
    
    //여행정보 게시글 삭제시 해당 좋아요 데이터 삭제
    @Transactional
    public void deleteTripLikeByTrip(Trip trip) {
        tripLikeRepository.deleteAllByTrip(trip);
    }

    private Member getMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private Trip getTrip(Long tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }
}
