package com.travelland.service.trip;

import com.travelland.document.TripSearchDoc;
import com.travelland.domain.member.Member;
import com.travelland.domain.trip.Trip;
import com.travelland.domain.trip.TripHashtag;
import com.travelland.dto.TripDto;
import com.travelland.dto.TripSearchDto;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.repository.member.MemberRepository;
import com.travelland.repository.trip.TripHashtagRepository;
import com.travelland.repository.trip.TripRepository;
import com.travelland.repository.trip.TripSearchRepository;
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
    private final TripSearchRepository tripSearchRepository;
    private final StringRedisTemplate redisTemplate;
    private final TripImageService tripImageService;
    private final TripLikeService tripLikeService;
    private final TripScrapService tripScrapService;

    private static final String TOTAL_ELEMENTS = "trip:totalElements";

    @Transactional
    public TripDto.Id createTrip(TripDto.Create requestDto, List<MultipartFile> imageList, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Trip trip = tripRepository.save(new Trip(requestDto, member));//여행정보 저장

        TripSearchDto.CreateRequest request = new TripSearchDto.CreateRequest(trip, requestDto.getHashTag());
        tripSearchRepository.save(new TripSearchDoc(request));

        if (!requestDto.getHashTag().isEmpty()) //해쉬태그 저장
            requestDto.getHashTag().forEach(hashtagTitle -> tripHashtagRepository.save(new TripHashtag(hashtagTitle, trip)));

        if (!imageList.isEmpty()) //여행정보 이미지 정보 저장
            tripImageService.createTripImage(imageList, trip);

        redisTemplate.opsForValue().increment(TOTAL_ELEMENTS);

        return new TripDto.Id(trip.getId());
    }

    public List<TripDto.GetList> getTripList(int page, int size, String sortBy, boolean isAsc) {
        return tripRepository.getTripList(page, size, sortBy, isAsc)
                        .stream()
                        .map(trip -> new TripDto.GetList(trip, tripImageService.getTripImageThumbnailUrl(trip)))
                        .toList();
    }

    @Transactional
    public TripDto.Get getTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        trip.increaseViewCount(); //조회수 증가

        // 해쉬태그 가져오기
        List<String> hashTag = tripHashtagRepository.findAllByTrip(trip).stream()
                .map(TripHashtag::getTitle).toList();

        List<String> imageUrlList = tripImageService.getTripImageUrl(trip);

        return new TripDto.Get(trip, hashTag, imageUrlList);
    }

    @Transactional
    public TripDto.Id updateTrip(Long tripId, TripDto.Update requestDto, List<MultipartFile> imageList, String email) {
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
            tripImageService.createTripImage(imageList, trip);

        //여행정보 수정
        trip.update(requestDto);

        return new TripDto.Id(trip.getId());
    }

    @Transactional
    public void deleteTrip(Long tripId, String email) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (!trip.getMember().getEmail().equals(email))
            throw new CustomException(ErrorCode.POST_DELETE_NOT_PERMISSION);

        // 여행정보 엔티티와 관련된 데이터 삭제
        tripImageService.deleteTripImage(trip);
        tripLikeService.deleteTripLike(trip);
        tripScrapService.deleteTripScrap(trip);

        tripHashtagRepository.deleteByTrip(trip);
        tripRepository.delete(trip);

        redisTemplate.opsForValue().decrement(TOTAL_ELEMENTS);
    }

    @Transactional(readOnly = true)
    public List<TripDto.GetByMember> getMyTripList(int page, int size, String email) {
        return tripRepository.getMyTripList(page, size, getMember(email))
                .stream()
                .map(trip -> new TripDto.GetByMember(trip, tripImageService.getTripImageThumbnailUrl(trip)))
                .toList();
    }

    public List<TripDto.GetList> searchTripByHashtag(String hashtag, int page, int size, String sortBy, boolean isAsc) {
        return tripRepository.searchTripByHashtag(hashtag, page, size, sortBy, isAsc)
                        .stream()
                        .map(trip -> new TripDto.GetList(trip, tripImageService.getTripImageThumbnailUrl(trip)))
                        .toList();
    }

    private Member getMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
