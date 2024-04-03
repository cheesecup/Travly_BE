package com.travelland.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.travelland.domain.*;
import com.travelland.dto.TripDto;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.repository.MemberRepository;
import com.travelland.repository.TripLikeRepository;
import com.travelland.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.travelland.domain.QTripLike.tripLike;

@Service
@RequiredArgsConstructor
public class TripLikeService {

    private final TripLikeRepository tripLikeRepository;
    private final MemberRepository memberRepository;
    private final TripRepository tripRepository;
    private final JPAQueryFactory jpaQueryFactory;

    //여행정보 좋아요 등록
    @Transactional
    public void registerTripLike(Long tripId, String email) {
        Member member = getMember(email);
        Trip trip = getTrip(tripId);

        tripLikeRepository.findByMemberAndTrip(member, trip)
                .ifPresentOrElse(
                        TripLike::registerLike, // 좋아요를 한번이라도 등록한적이 있을경우
                        () -> tripLikeRepository.save(new TripLike(member, trip)) // 최초로 좋아요를 등록하는 경우
                );
        trip.increaseLikeCount();
    }

    //여행정보 좋아요 취소
    @Transactional
    public void cancelTripLike(Long tripId, String email) {
        Member member = getMember(email);
        Trip trip = getTrip(tripId);

        TripLike tripLike = tripLikeRepository.findByMemberAndTrip(member, trip)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_LIKE_NOT_FOUND));
        tripLike.cancelLike();
        trip.decreaseLikeCount();
    }

    //여행정보 좋아요 목록 조회
    @Transactional(readOnly = true)
    public List<TripDto.GetTripLikeListResponse> getTripLikeList(int page, int size, String email) {
        Member member = getMember(email);

        List<TripLike> tripLikeList = jpaQueryFactory.selectFrom(tripLike)
                .where(tripLike.member.eq(member), tripLike.isDeleted.eq(false))
                .orderBy(tripLike.trip.createdAt.desc())
                .limit(size)
                .offset((long) (page - 1) * size)
                .fetch();

        return tripLikeList.stream().map(tripLike -> new TripDto.GetTripLikeListResponse(tripLike.getTrip(), tripLike.getMember()))
                .collect(Collectors.toList());
    }
    
    //좋아요 데이터 삭제
    @Transactional
    public void deleteTripLike(Trip trip) {
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
