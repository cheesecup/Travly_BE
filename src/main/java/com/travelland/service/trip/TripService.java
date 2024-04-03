package com.travelland.service.trip;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.travelland.domain.Member;
import com.travelland.domain.Trip;
import com.travelland.domain.TripHashtag;
import com.travelland.dto.TripDto;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.repository.MemberRepository;
import com.travelland.repository.trip.TripHashtagRepository;
import com.travelland.repository.trip.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final MemberRepository memberRepository;
    private final TripHashtagRepository tripHashtagRepository;
    private final TripImageService tripImageService;
    private final TripLikeService tripLikeService;
    private final TripScrapService tripScrapService;

    @Transactional
    public TripDto.Id createTrip(TripDto.Create requestDto, List<MultipartFile> imageList, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Trip trip = tripRepository.save(new Trip(requestDto, member));//여행정보 저장

        if (!requestDto.getHashTag().isEmpty()) //해쉬태그 저장
            requestDto.getHashTag().forEach(hashtagTitle -> tripHashtagRepository.save(new TripHashtag(hashtagTitle, trip)));

//        if (!imageList.isEmpty()) //여행정보 이미지 정보 저장
//            tripImageService.createTripImage(imageList, trip);

        return new TripDto.Id(trip.getId());
    }

    @Transactional(readOnly = true)
    public List<TripDto.GetList> getTripList(int page, int size, String sort, boolean ASC) {
        return tripRepository.getTripList(page, size, sort, ASC)
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
    }

    @Transactional(readOnly = true)
    public List<TripDto.GetByMember> getMyTripList(int page, int size, String email) {
        return tripRepository.getMyTripList(page, size, getMember(email))
                .stream()
                .map(trip -> new TripDto.GetByMember(trip, tripImageService.getTripImageThumbnailUrl(trip)))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TripDto.GetList> searchTripByHashtag(String hashtag, int page, int size, String sort, boolean ASC) {
        return tripRepository.searchTripByHashtag(hashtag, page, size, sort, ASC)
                .stream()
                .map(trip -> new TripDto.GetList(trip, tripImageService.getTripImageThumbnailUrl(trip)))
                .toList();

    }

    private Member getMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
