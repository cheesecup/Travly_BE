package com.travelland.service;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.travelland.domain.Member;
import com.travelland.domain.QTrip;
import com.travelland.domain.Trip;
import com.travelland.dto.TripDto;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.repository.MemberRepository;
import com.travelland.repository.TripRepository;
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
    private final TripImageService tripImageService;
    private final TripHashTagService tripHashTagService;
    private final JPAQueryFactory jpaQueryFactory;

    @Transactional
    public TripDto.CreateResponse createTrip(TripDto.CreateRequest requestDto, List<MultipartFile> imageList, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Trip trip = tripRepository.save(new Trip(requestDto, member));//여행정보 저장

        if (!requestDto.getHashTag().isEmpty()) //해쉬태그 저장
            requestDto.getHashTag().forEach(hashtagTitle -> tripHashTagService.createHashTag(hashtagTitle, trip));

        if (!imageList.isEmpty()) //여행정보 이미지 정보 저장
            tripImageService.createTripImage(imageList, trip);

        return new TripDto.CreateResponse(trip.getId());
    }

    @Transactional(readOnly = true)
    public List<TripDto.GetListResponse> getTripList(int page, int size, String sort, boolean ASC) {
        Order order = (ASC) ? Order.ASC : Order.DESC;
        OrderSpecifier orderSpecifier = createOrderSpecifier(sort, order);
        List<Trip> tripList = jpaQueryFactory.selectFrom(QTrip.trip)
                .orderBy(orderSpecifier, QTrip.trip.id.desc())
                .limit(size)
                .offset((long) (page - 1) * size)
                .fetch();

        // 게시글 목록 조회된 여행정보의 썸네일 URL 가져오기
        return tripList.stream()
                .map(trip -> new TripDto.GetListResponse(trip, tripImageService.getTripImageThumbnailUrl(trip)))
                .collect(Collectors.toList());
    }

    @Transactional
    public TripDto.GetResponse getTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        trip.increaseViewCount(); //조회수 증가
        List<String> hashTag = tripHashTagService.getHashTagList(trip);
        List<String> imageUrlList = tripImageService.getTripImageUrl(trip);

        return new TripDto.GetResponse(trip, hashTag, imageUrlList);
    }

    @Transactional
    public TripDto.UpdateResponse updateTrip(Long tripId, TripDto.UpdateRequest requestDto, List<MultipartFile> imageList, String email) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (!trip.getMember().getEmail().equals(email))
            throw new CustomException(ErrorCode.POST_UPDATE_NOT_PERMISSION);

        //해쉬태그 수정
        tripHashTagService.deleteHashTag(trip);

        if (!requestDto.getHashTag().isEmpty())
            requestDto.getHashTag().forEach(hashtagTitle -> tripHashTagService.createHashTag(hashtagTitle, trip));

        //이미지 수정
        tripImageService.deleteTripImage(trip);

        if (!imageList.isEmpty())
            tripImageService.createTripImage(imageList, trip);

        //여행정보 수정
        trip.update(requestDto);

        return new TripDto.UpdateResponse(trip.getId());
    }

    @Transactional
    public void deleteTrip(Long tripId, String email) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (!trip.getMember().getEmail().equals(email))
            throw new CustomException(ErrorCode.POST_DELETE_NOT_PERMISSION);

        tripHashTagService.deleteHashTag(trip);
        tripImageService.deleteTripImage(trip);
        tripRepository.delete(trip);
    }

    // 목록 정렬 방식, 기준 설정 메서드
    private OrderSpecifier createOrderSpecifier(String sort, Order order) {
        return switch (sort) {
            case "viewCount" -> new OrderSpecifier<>(order, QTrip.trip.viewCount);
            case "title" -> new OrderSpecifier<>(order, QTrip.trip.title);
            default -> new OrderSpecifier<>(order, QTrip.trip.createdAt);
        };
    }

}
