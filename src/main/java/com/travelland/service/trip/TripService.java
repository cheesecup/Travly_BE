package com.travelland.service.trip;

import com.travelland.domain.member.Member;
import com.travelland.domain.trip.Trip;
import com.travelland.domain.trip.TripHashtag;
import com.travelland.dto.trip.TripDto;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.repository.member.MemberRepository;
import com.travelland.repository.trip.TripHashtagRepository;
import com.travelland.repository.trip.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.travelland.constant.Constants.*;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final MemberRepository memberRepository;
    private final TripHashtagRepository tripHashtagRepository;
    private final RedisTemplate<String,String> redisTemplate;
    private final TripImageService tripImageService;
    private final TripLikeService tripLikeService;
    private final TripScrapService tripScrapService;
    private final TripSearchService tripSearchService;

    @Transactional
    public TripDto.Id createTrip(TripDto.Create requestDto, MultipartFile thumbnail, List<MultipartFile> imageList, String email) {
        Member member = getMember(email);
        Trip trip = tripRepository.save(new Trip(requestDto, member));

        if (!requestDto.getHashTag().isEmpty()) //해쉬태그 저장
            requestDto.getHashTag().forEach(hashtagTitle -> tripHashtagRepository.save(new TripHashtag(hashtagTitle, trip)));

        String thumbnailUrl = "";
        if (!thumbnail.isEmpty()) //여행정보 이미지 정보 저장
            thumbnailUrl = tripImageService.createTripImage(thumbnail, imageList, trip);

        tripSearchService.createTripDocument(trip, requestDto.getHashTag(), thumbnailUrl); //ES 저장

        return new TripDto.Id(trip.getId());
    }

    @Transactional
    public TripDto.Get getTrip(Long tripId, String email) {
        Trip trip = tripRepository.getTripWithMember(tripId, false).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        Member member = trip.getMember();

        if (!trip.isPublic() && member.getEmail().equals(email)) { //비공개 글인 경우
            throw new CustomException(ErrorCode.POST_ACCESS_NOT_PERMISSION);
        }

        List<String> hashtagList = getHashtags(trip).stream().map(TripHashtag::getTitle).toList();
        List<String> imageUrlList = tripImageService.getTripImageUrl(trip);

        boolean isLike = false;
        boolean isScrap = false;
        boolean isWriter = member.getEmail().equals(email);

        if(email.isEmpty())
            return new TripDto.Get(trip, member, hashtagList, imageUrlList, isLike, isScrap, isWriter);

        //로그인한 경우
        //스크랩/좋아요 여부 확인
        isLike = tripLikeService.statusTripLike(tripId, email);
        isScrap = tripScrapService.statusTripScrap(tripId, email);

        //조회수 증가
        Long result = redisTemplate.opsForSet().add(TRIP_VIEW_COUNT + tripId, email);

        if (result != null && result == 1L) {
            trip.increaseViewCount();
            Long view = redisTemplate.opsForSet().size(TRIP_VIEW_COUNT + tripId);

            if(view != null)
                redisTemplate.opsForZSet().add(VIEW_RANK, tripId.toString(), view);
        }

        return new TripDto.Get(trip, member, hashtagList, imageUrlList, isLike, isScrap, isWriter);
    }

    @Transactional
    public TripDto.Id updateTrip(Long tripId, TripDto.Update requestDto, MultipartFile thumbnail, List<MultipartFile> imageList, String email) {
        Trip trip = getTrip(tripId);

        if (!trip.getMember().getEmail().equals(email))
            throw new CustomException(ErrorCode.POST_UPDATE_NOT_PERMISSION);

        //해쉬태그 수정
        tripHashtagRepository.deleteByTrip(trip);

        if (!requestDto.getHashTag().isEmpty())
            requestDto.getHashTag().forEach(hashtagTitle -> tripHashtagRepository.save(new TripHashtag(hashtagTitle, trip)));

        //이미지 수정
        tripImageService.deleteTripImage(trip);

        if (!imageList.isEmpty())
            tripImageService.createTripImage(thumbnail, imageList, trip);

        //여행정보 수정
        trip.update(requestDto);

        return new TripDto.Id(trip.getId());
    }

    @Transactional
    public void deleteTrip(Long tripId, String email) {
        Trip trip = getTrip(tripId);

        if (!trip.getMember().getEmail().equals(email))
            throw new CustomException(ErrorCode.POST_DELETE_NOT_PERMISSION);

        // ES
        tripSearchService.deleteTrip(tripId);

        // 여행정보 엔티티와 관련된 데이터 삭제
        tripImageService.deleteTripImage(trip);
        tripLikeService.deleteTripLike(trip);
        tripScrapService.deleteTripScrap(trip);
        tripHashtagRepository.deleteByTrip(trip);

        trip.delete();

        redisTemplate.opsForValue().decrement(TRIP_TOTAL_ELEMENTS);
    }

    public List<TripDto.Top10> getRankByViewCount(long size){
        Set<String> ranks = redisTemplate.opsForZSet()
                .reverseRange(VIEW_RANK,0L,size+5L);

        if (ranks == null)
            return new ArrayList<>();

        List<TripDto.Top10> result =  tripSearchService.getRankByViewCount(ranks.stream()
                .map(Long::parseLong)
                .toList());

        if(result.size()>=10)
            return result.subList(0,10);

        return result;
    }

    private List<TripHashtag> getHashtags(Trip trip){
        return tripHashtagRepository.findAllByTrip(trip);
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