package com.travelland.service.trip;

import com.travelland.domain.member.Member;
import com.travelland.domain.trip.Trip;
import com.travelland.domain.trip.TripScrap;
import com.travelland.dto.trip.TripDto;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.repository.member.MemberRepository;
import com.travelland.repository.trip.TripRepository;
import com.travelland.repository.trip.TripScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripScrapService {

    private final TripScrapRepository tripScrapRepository;
    private final MemberRepository memberRepository;
    private final TripRepository tripRepository;


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
    public List<TripDto.Scraps> getTripScrapList(int page, int size, String email) {
        return tripScrapRepository.getScrapListByMember(getMember(email),size, page)
                .stream().map(TripDto.Scraps::new).toList();
    }

    //스크랩 데이터 삭제
    @Transactional
    public void deleteTripScrap(Trip trip) {
        tripScrapRepository.deleteByTrip(trip);
    }

    public boolean statusTripScrap(String email, Long tripId) {
        Member member = getMember(email);
        Trip trip = getTrip(tripId);

        return tripScrapRepository.existsByMemberAndTripAndIsDeleted(member, trip, false);
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
