package com.travelland.service.trip;

import com.travelland.domain.member.Member;
import com.travelland.domain.trip.Trip;
import com.travelland.domain.trip.TripLike;
import com.travelland.dto.trip.TripDto;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.repository.member.MemberRepository;
import com.travelland.repository.trip.TripLikeRepository;
import com.travelland.repository.trip.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.travelland.constant.Constants.TRIP_LIKES_TRIP_ID;
import static com.travelland.constant.Constants.TRIP_SCRAPS_TRIP_ID;

@Service
@RequiredArgsConstructor
public class TripLikeService {

    private final TripLikeRepository tripLikeRepository;
    private final MemberRepository memberRepository;
    private final TripRepository tripRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public void registerTripLike(Long tripId, String email) {
        Member member = getMember(email);
        Trip trip = getTrip(tripId);
        tripLikeRepository.findByMemberAndTrip(member, trip)
                .ifPresentOrElse(
                        TripLike::registerLike,
                        () -> tripLikeRepository.save(new TripLike(member, trip))
                );
        redisTemplate.opsForSet().add(TRIP_LIKES_TRIP_ID + tripId, email);
    }

    //여행정보 좋아요 취소
    @Transactional
    public void cancelTripLike(Long tripId, String email) {
        Member member = getMember(email);
        Trip trip = getTrip(tripId);

        TripLike tripLike = tripLikeRepository.findByMemberAndTrip(member, trip)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_LIKE_NOT_FOUND));
        tripLike.cancelLike();
        redisTemplate.opsForSet().remove(TRIP_LIKES_TRIP_ID + tripId, email);
    }

    //여행정보 좋아요 목록 조회
    @Transactional(readOnly = true)
    public List<TripDto.Likes> getTripLikeList(int page, int size, String email) {
        return tripLikeRepository.getLikeListByMember(getMember(email), size, page)
                .stream().map(TripDto.Likes::new).toList();
    }

    //게시글 좋아요 여부 확인
    public boolean statusTripLike(Long tripId, String email) {
        if(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(TRIP_LIKES_TRIP_ID + tripId, email)))
            return true;

        Optional<TripLike> tripLike = tripLikeRepository.findByMemberAndTrip(getMember(email),getTrip(tripId));
        if(tripLike.isPresent()) {
            redisTemplate.opsForSet().add(TRIP_SCRAPS_TRIP_ID + tripId, email);
            return true;
        }
        return false;
    }

    //스크랩 데이터 삭제
    @Transactional
    public void deleteTripLike(Trip trip) {
        tripLikeRepository.deleteByTrip(trip);
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