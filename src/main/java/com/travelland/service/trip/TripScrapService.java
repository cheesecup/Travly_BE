package com.travelland.service.trip;

import com.travelland.domain.member.Member;
import com.travelland.domain.trip.Trip;
import com.travelland.domain.trip.TripScrap;
import com.travelland.dto.trip.TripDto;
import com.travelland.esdoc.TripSearchDoc;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.repository.member.MemberRepository;
import com.travelland.repository.trip.TripRepository;
import com.travelland.repository.trip.TripScrapRepository;
import com.travelland.repository.trip.TripSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.travelland.constant.Constants.TRIP_SCRAPS_TRIP_ID;

@Service
@RequiredArgsConstructor
public class TripScrapService {

    private final TripScrapRepository tripScrapRepository;
    private final MemberRepository memberRepository;
    private final TripRepository tripRepository;
    private final TripSearchRepository tripSearchESRepository;
    private final RedisTemplate<String, String> redisTemplate;

    //여행정보 스크랩 등록
    @Transactional
    public void registerTripScrap(Long tripId, String email) {
        Member member = getMember(email);
        Trip trip = getTrip(tripId);

        tripScrapRepository.findByMemberAndTrip(member, trip)
                .ifPresentOrElse(
                        TripScrap::registerScrap,
                        () -> tripScrapRepository.save(new TripScrap(member, trip))
                );

        redisTemplate.opsForSet().add(TRIP_SCRAPS_TRIP_ID + tripId, email);
    }

    //여행정보 스크랩 취소
    @Transactional
    public void cancelTripScrap(Long tripId, String email) {
        Member member = getMember(email);
        Trip trip = getTrip(tripId);

        TripScrap tripScrap = tripScrapRepository.findByMemberAndTrip(member, trip)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_SCRAP_NOT_FOUND));
        tripScrap.cancelScrap();

        redisTemplate.opsForSet().remove(TRIP_SCRAPS_TRIP_ID + tripId, email);
    }
    
    //스크랩한 여행정보 목록 조회
    @Transactional(readOnly = true)
    public List<TripDto.Scraps> getTripScrapList(int page, int size, String email) {
        List<TripSearchDoc> scrapList = tripScrapRepository.getScrapListByMember(getMember(email), size, page).stream()
                .map(tripScrap -> tripSearchESRepository.findByTripId(tripScrap.getTrip().getId())).toList();

        return scrapList.stream().map(TripDto.Scraps::new).toList();
    }

    //스크랩 데이터 삭제
    @Transactional
    public void deleteTripScrap(Trip trip) {
        tripScrapRepository.deleteByTrip(trip);
    }
    
    //게시글 스크랩 여부 확인
    public boolean statusTripScrap(Long tripId, String email) {
        if(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(TRIP_SCRAPS_TRIP_ID + tripId, email)))
            return true;

        Optional<TripScrap> tripScrap = tripScrapRepository.findByMemberAndTrip(getMember(email),getTrip(tripId));
        if(tripScrap.isPresent()) {
            redisTemplate.opsForSet().add(TRIP_SCRAPS_TRIP_ID + tripId, email);
            return true;
        }
        return false;
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