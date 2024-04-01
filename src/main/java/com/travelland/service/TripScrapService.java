package com.travelland.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.travelland.domain.*;
import com.travelland.dto.TripDto;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.repository.MemberRepository;
import com.travelland.repository.TripRepository;
import com.travelland.repository.TripScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripScrapService {

    private final TripScrapRepository tripScrapRepository;
    private final MemberRepository memberRepository;
    private final TripRepository tripRepository;
    private final JPAQueryFactory jpaQueryFactory;

    //여행정보 스크랩 추가
    @Transactional
    public void createTripScrap(Long tripId, String email) {
        Member member = getMember(email);
        Trip trip = getTrip(tripId);

        tripScrapRepository.save(new TripScrap(member, trip));
    }

    //여행정보 스크랩 삭제
    @Transactional
    public void deleteTripScrap(Long tripId, String email) {
        Member member = getMember(email);
        Trip trip = getTrip(tripId);

        tripScrapRepository.deleteByMemberAndTrip(member, trip);
    }
    
    //스크랩한 여행정보 목록 조회
    @Transactional(readOnly = true)
    public List<TripDto.GetTripScrapListResponse> getTripScrapList(int page, int size, String sort, boolean ASC, String email) {
        Member member = getMember(email);

        List<TripScrap> tripScrapList = jpaQueryFactory.selectFrom(QTripScrap.tripScrap)
                .where(QTripScrap.tripScrap.member.eq(member))
                .orderBy(QTripScrap.tripScrap.trip.createdAt.desc())
                .limit(size)
                .offset((long) (page - 1) * size)
                .fetch();

        return tripScrapList.stream().map(tripScrap -> new TripDto.GetTripScrapListResponse(tripScrap.getTrip(), tripScrap.getMember()))
                .collect(Collectors.toList());
    }

    //여행정보 게시글 삭제시 해당 스크랩 데이터 삭제
    @Transactional
    public void deleteTripScrapByTrip(Trip trip) {
        tripScrapRepository.deleteAllByTrip(trip);
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
