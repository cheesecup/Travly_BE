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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final MemberRepository memberRepository;
    private final TripHashtagRepository tripHashtagRepository;
    private final StringRedisTemplate redisTemplate;
    private final TripImageService tripImageService;
    private final TripLikeService tripLikeService;
    private final TripScrapService tripScrapService;
    private final TripSearchService tripSearchService;

    private static final String TRIP_TOTAL_ELEMENTS = "trip:totalElements";
    private static final String VIEW_COUNT = "viewCount:tripId:";
    private static final String VIEW_RANK = "tripViewRank";

    @Transactional
    public TripDto.Id createTrip(TripDto.Create requestDto, MultipartFile thumbnail, List<MultipartFile> imageList, String email) {
        Member member = getMember(email);
        Trip trip = tripRepository.save(new Trip(requestDto, member));

        if (!requestDto.getHashTag().isEmpty()) //해쉬태그 저장
            requestDto.getHashTag().forEach(hashtagTitle -> tripHashtagRepository.save(new TripHashtag(hashtagTitle, trip)));

        String thumbnailUrl = "";
        if (!thumbnail.isEmpty()) //여행정보 이미지 정보 저장
            thumbnailUrl = tripImageService.createTripImage(thumbnail, imageList, trip);

        redisTemplate.opsForValue().increment(TRIP_TOTAL_ELEMENTS);

        tripSearchService.createTripDocument(trip, requestDto.getHashTag(), member, thumbnailUrl, member.getThumbnailProfileImage()); //ES 저장

        return new TripDto.Id(trip.getId());
    }

    @Transactional
    public TripDto.Get getTrip(Long tripId, String email) {
        Trip trip = tripRepository.findByIdAndIsDeletedAndIsPublic(tripId, false, true).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        List<String> hashtagList = tripHashtagRepository.findAllByTrip(trip).stream().map(TripHashtag::getTitle).toList();
        List<String> imageUrlList = tripImageService.getTripImageUrl(trip);

        boolean isLike = false;
        boolean isScrap = false;
        if (!email.isEmpty()) { //로그인한 경우
            //스크랩/좋아요 여부 확인
            isLike = tripLikeService.statusTripLike(tripId, email);
            isScrap = tripScrapService.statusTripScrap(tripId, email);

            //조회수 증가
            Long result = redisTemplate.opsForSet().add(VIEW_COUNT + tripId, email); //redis 조회수 증가

            if (result != null && result == 1L) {
                Long view = redisTemplate.opsForSet().size(VIEW_COUNT + tripId); //redis 조회수 Get
                redisTemplate.opsForZSet().add(VIEW_RANK, tripId.toString(), view);
            }
        }

//        trip.increaseViewCount(); //MySQL
//        tripSearchService.increaseViewCount(tripId);

        return new TripDto.Get(trip, hashtagList, imageUrlList, isLike, isScrap);
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

    private Member getMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private Trip getTrip(Long tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }
}
