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

    private static final String TOTAL_ELEMENTS = "trip:totalElements";

    @Transactional
    public TripDto.Id createTrip(TripDto.Create requestDto, MultipartFile thumbnail, List<MultipartFile> imageList, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Trip trip = tripRepository.save(new Trip(requestDto, member));

        if (!requestDto.getHashTag().isEmpty()) //해쉬태그 저장
            requestDto.getHashTag().forEach(hashtagTitle -> tripHashtagRepository.save(new TripHashtag(hashtagTitle, trip)));

        String thumbnailUrl = "";
        if (!thumbnail.isEmpty()) //여행정보 이미지 정보 저장
            thumbnailUrl = tripImageService.createTripImage(thumbnail, imageList, trip);

        tripSearchService.createTripDocument(trip, requestDto.getHashTag(), member, thumbnailUrl); //ES

        redisTemplate.opsForValue().increment(TOTAL_ELEMENTS);

        return new TripDto.Id(trip.getId());
    }

    @Transactional
    public TripDto.Get getTrip(Long tripId) {
//        Trip trip = tripRepository.findByIdAndIsDeletedAndIsPublic(tripId, false, true).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        Trip trip = tripRepository.findByIdAndIsDeleted(tripId, false).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        
        // 조회수 증가
        trip.increaseViewCount();
        tripSearchService.increaseViewCount(tripId);

        // 해쉬태그 가져오기
        List<String> hashTag = tripHashtagRepository.findAllByTrip(trip).stream()
                .map(TripHashtag::getTitle).toList();

        List<String> imageUrlList = tripImageService.getTripImageUrl(trip);

        boolean isLike = tripLikeService.statusTripLike("test@test.com", tripId);
        boolean isScrap = tripScrapService.statusTripScrap("test@test.com", tripId);

        return new TripDto.Get(trip, hashTag, imageUrlList, isLike, isScrap);
    }

    @Transactional
    public TripDto.Id updateTrip(Long tripId, TripDto.Update requestDto, MultipartFile thumbnail, List<MultipartFile> imageList, String email) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

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
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

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

        redisTemplate.opsForValue().decrement(TOTAL_ELEMENTS);
    }

    private Member getMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
