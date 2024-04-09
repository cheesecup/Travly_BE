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

import java.util.*;

@Service
@RequiredArgsConstructor
public class TripLikeService {

    private final TripLikeRepository tripLikeRepository;
    private final MemberRepository memberRepository;
    private final TripRepository tripRepository;
    private final RedisTemplate<String, String>  redisTemplate;

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
        redisTemplate.opsForSet().add("tripLikes:" + tripId, email);
        redisTemplate.opsForSet().add("userLikes:" + email, tripId.toString());
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
        redisTemplate.opsForSet().remove("tripLikes:" + tripId, email);
        redisTemplate.opsForSet().remove("userLikes:" + email, tripId.toString());
    }

    //여행정보 좋아요 목록 조회
    @Transactional(readOnly = true)
    public List<TripDto.Likes> getTripLikeList(int page, int size, String email) {
        return tripLikeRepository.getLikeListByMember(getMember(email), size, page)
                .stream().map(TripDto.Likes::new).toList();
    }

    public Long getLikeCount(long tripId){
        return redisTemplate.opsForSet().size("tripLikes:" + tripId);
    }

    public List<Long> recommandTrips(String email, Long tripId) {
        // tripLikes 집합의 모든 멤버를 가져옴
        Set<String> tripLikesMembers = redisTemplate.opsForSet().members("tripLikes:" + tripId);
        tripLikesMembers.remove(email);

        Set<String> intersectedPosts = null;

        for(String member : tripLikesMembers) {
            Set<String> userLikes = redisTemplate.opsForSet().members("userLikes:" + member);

            if (userLikes == null) return new ArrayList<>();

            if (intersectedPosts == null) {
                intersectedPosts = new HashSet<>(userLikes);
                intersectedPosts.remove(tripId.toString());
                continue;
            }

            intersectedPosts.retainAll(userLikes);

            if (intersectedPosts.size() > 6)
                break;
        }
        if (intersectedPosts == null)
            return new ArrayList<>();


        return intersectedPosts.stream().map(Long::parseLong).toList();
    }

    public List<Long> recommendPlusTrips(String email, Long tripId) {

        Set<String> tripLikesMembers = redisTemplate.opsForSet().members("tripLikes:" + tripId);
        tripLikesMembers.remove(email);

        Map<Long, Integer> intersectedPostsMap = new HashMap<>();

        tripLikesMembers.parallelStream()
                .forEach(member -> redisTemplate.opsForSet().members("userLikes:" + member)
                        .stream()
                        .map(Long::parseLong)
                        .filter(recommendId -> !recommendId.equals(tripId))
                        .forEach(recommendId -> intersectedPostsMap.merge(recommendId, 1, Integer::sum)));

        return intersectedPostsMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();
    }

    public boolean statusTripLike(String email, Long tripId) {
        Member member = getMember(email);
        Trip trip = getTrip(tripId);

        return tripLikeRepository.existsByMemberAndTripAndIsDeleted(member, trip, false);
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