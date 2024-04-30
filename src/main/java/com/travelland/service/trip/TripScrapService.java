package com.travelland.service.trip;

import com.travelland.domain.member.Member;
import com.travelland.domain.trip.Trip;
import com.travelland.domain.trip.TripScrap;
import com.travelland.dto.trip.TripDto;
import com.travelland.esdoc.TripSearchDoc;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
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
    private final TripRepository tripRepository;
    private final TripSearchRepository tripSearchESRepository;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 여행후기 스크랩 등록
     * @param tripId 여행후기 id
     * @param loginMember 로그인한 회원 정보
     */
    @Transactional
    public void registerTripScrap(Long tripId, Member loginMember) {
        Trip trip = getTrip(tripId);

        tripScrapRepository.findByMemberAndTrip(loginMember, trip)
                .ifPresentOrElse(
                        TripScrap::registerScrap,
                        () -> tripScrapRepository.save(new TripScrap(loginMember, trip))
                );

        redisTemplate.opsForSet().add(TRIP_SCRAPS_TRIP_ID + tripId, loginMember.getEmail());
    }

    /**
     * 여행후기 스크랩 취소
     * @param tripId 여행후기 id
     * @param loginMember 로그인한 회원 정보
     */
    @Transactional
    public void cancelTripScrap(Long tripId, Member loginMember) {
        Trip trip = getTrip(tripId);

        TripScrap tripScrap = tripScrapRepository.findByMemberAndTrip(loginMember, trip)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_SCRAP_NOT_FOUND));
        tripScrap.cancelScrap();

        redisTemplate.opsForSet().remove(TRIP_SCRAPS_TRIP_ID + tripId, loginMember.getEmail());
    }

    /**
     * 스크랩한 여행후기 게시글 목록 조회
     * @param page 조회할 페이지 번호
     * @param size 한 페이지에 보여지는 게시글 수
     * @param loginMember 로그인한 회원 정보
     * @return 조회된 여행후기 목록
     */
    @Transactional(readOnly = true)
    public List<TripDto.Scraps> getTripScrapList(int page, int size, Member loginMember) {
        List<TripSearchDoc> scrapList = tripScrapRepository.getScrapListByMember(loginMember, size, page).stream()
                .map(tripScrap -> tripSearchESRepository.findByTripId(tripScrap.getTrip().getId())).toList();

        return scrapList.stream().map(TripDto.Scraps::new).toList();
    }

    /**
     * 스크랩 삭제
     * @param trip 삭제할 여행후기
     */
    @Transactional
    public void deleteTripScrap(Trip trip) {
        tripScrapRepository.deleteByTrip(trip);
    }

    /**
     * 여행후기 스크랩 상태 확인
     * @param tripId 조회할 여행후기 id
     * @param loginMember 로그인한 회원정보
     * @return 스크랩 여부
     */
    public boolean statusTripScrap(Long tripId, Member loginMember) {
        if(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(TRIP_SCRAPS_TRIP_ID + tripId, loginMember.getEmail())))
            return true;

        Optional<TripScrap> tripScrap = tripScrapRepository.findByMemberAndTripAndIsDeleted(loginMember,getTrip(tripId),false);
        if(tripScrap.isPresent()) {
            redisTemplate.opsForSet().add(TRIP_SCRAPS_TRIP_ID + tripId, loginMember.getEmail());
            return true;
        }
        return false;
    }

    /**
     * 여행후기 정보 조회
     * @param tripId 조회할 여행후기 id
     * @return 조회한 여행후기
     */
    private Trip getTrip(Long tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }
}