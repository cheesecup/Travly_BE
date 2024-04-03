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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.travelland.domain.QTripScrap.*;

@Service
@RequiredArgsConstructor
public class TripScrapService {

    private final TripScrapRepository tripScrapRepository;
    private final MemberRepository memberRepository;
    private final TripRepository tripRepository;
    private final JPAQueryFactory jpaQueryFactory;

    //여행정보 스크랩 등록
    @Transactional
    public void registerTripScrap(Long tripId, String email) {
        Member member = getMember(email);
        Trip trip = getTrip(tripId);

        tripScrapRepository.findByMemberAndTrip(member, trip)
                .ifPresentOrElse(
                        TripScrap::registerScrap, // 스크랩을 한번이라도 등록한적이 있을경우
                        () -> tripScrapRepository.save(new TripScrap(member, trip)) // 최초로 스크랩을 등록하는 경우
                );
    }

    //여행정보 스크랩 취소
    @Transactional
    public void cancelTripScrap(Long tripId, String email) {
        Member member = getMember(email);
        Trip trip = getTrip(tripId);

        TripScrap tripScrap = tripScrapRepository.findByMemberAndTrip(member, trip)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_SCRAP_NOT_FOUND));
        tripScrap.cancelScrap();
    }
    
    //스크랩한 여행정보 목록 조회
    @Transactional(readOnly = true)
    public List<TripDto.GetTripScrapListResponse> getTripScrapList(int page, int size, String email) {
        Member member = getMember(email);

        List<TripScrap> tripScrapList = jpaQueryFactory.selectFrom(tripScrap)
                .where(tripScrap.member.eq(member), tripScrap.isDeleted.eq(false))
                .orderBy(tripScrap.trip.createdAt.desc())
                .limit(size)
                .offset((long) (page - 1) * size)
                .fetch();

        return tripScrapList.stream().map(tripScrap -> new TripDto.GetTripScrapListResponse(tripScrap.getTrip(), tripScrap.getMember()))
                .collect(Collectors.toList());
    }

    //스크랩 데이터 삭제
    @Transactional
    public void deleteTripScrap(Trip trip) {
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
