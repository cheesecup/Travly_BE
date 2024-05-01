package com.travelland.service.trip;

import com.travelland.domain.member.Member;
import com.travelland.domain.trip.Trip;
import com.travelland.domain.trip.TripHashtag;
import com.travelland.dto.trip.TripDto;
import com.travelland.esdoc.TripSearchDoc;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.repository.member.MemberRepository;
import com.travelland.repository.trip.TripHashtagRepository;
import com.travelland.repository.trip.TripRepository;
import com.travelland.repository.trip.TripSearchRepository;
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
    private final TripHashtagRepository tripHashtagRepository;
    private final MemberRepository memberRepository;
    private final TripSearchRepository tripSearchRepository;
    private final RedisTemplate<String,String> redisTemplate;
    private final TripImageService tripImageService;
    private final TripLikeService tripLikeService;
    private final TripScrapService tripScrapService;
    private final TripSearchService tripSearchService;

    /**
     * 회원이 입력한 여행후기 게시글 저장
     * @param requestDto 회원이 입력한 여행후기 정보
     * @param thumbnail 여행후기 게시글 썸네일 이미지
     * @param imageList 여행후기 게시글 추가 이미지
     * @param loginMember 로그인한 회원 정보
     * @return 생성된 여행후기 게시글 id
     */
    @Transactional
    public TripDto.Id createTrip(TripDto.Create requestDto, MultipartFile thumbnail, List<MultipartFile> imageList, Member loginMember) {
        Trip trip = tripRepository.save(new Trip(requestDto, loginMember));

        if (!requestDto.getHashTag().isEmpty())
            requestDto.getHashTag().forEach(hashtagTitle -> tripHashtagRepository.save(new TripHashtag(hashtagTitle, trip)));

        String thumbnailUrl = "";
        if (!thumbnail.isEmpty()) //여행정보 이미지 정보 저장
            thumbnailUrl = tripImageService.createTripImage(thumbnail, imageList, trip);

        tripSearchService.createTripDocument(trip, requestDto.getHashTag(), thumbnailUrl); //ES 저장

        return new TripDto.Id(trip.getId());
    }

    /**
     * 여행후기의 상세 내용 조회
     * @param tripId 조회하려는 게시글 id
     * @param loginMemberEmail 조회 요청한 회원 이메일
     * @return 여행후기의 상세 내용
     */
    @Transactional
    public TripDto.Get getTrip(Long tripId, String loginMemberEmail) {
        Trip trip = tripRepository.findByIdAndIsDeleted(tripId, false).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        Member writer = trip.getMember(); //게시글을 작성한 회원정보

        if (!trip.isPublic() && !writer.getEmail().equals(loginMemberEmail)) { //비공개 글인 경우
            throw new CustomException(ErrorCode.POST_ACCESS_NOT_PERMISSION);
        }

        List<String> hashtagList = getHashtags(trip).stream().map(TripHashtag::getTitle).toList();
        List<String> imageUrlList = tripImageService.getTripImageUrl(trip);

        boolean isLike = false;
        boolean isScrap = false;
        boolean isWriter = writer.getEmail().equals(loginMemberEmail);

        if(loginMemberEmail.isEmpty())
            return new TripDto.Get(trip, writer, hashtagList, imageUrlList, isLike, isScrap, isWriter);

        //로그인한 경우
        //스크랩/좋아요 여부 확인
        Member loginMember = memberRepository.findByEmail(loginMemberEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        isLike = tripLikeService.statusTripLike(tripId, loginMember);
        isScrap = tripScrapService.statusTripScrap(tripId, loginMember);

        //조회수 증가
        Long result = redisTemplate.opsForSet().add(TRIP_VIEW_COUNT + tripId, loginMember.getEmail());

        if (result != null && result == 1L) {
            trip.increaseViewCount();
            Long view = redisTemplate.opsForSet().size(TRIP_VIEW_COUNT + tripId);

            if(view != null)
                redisTemplate.opsForZSet().add(VIEW_RANK, tripId.toString(), view);
        }

        return new TripDto.Get(trip, writer, hashtagList, imageUrlList, isLike, isScrap, isWriter);
    }

    /**
     * 여행후기 게시글 내용 수정
     * @param tripId 수정하고자 하는 게시글 id
     * @param requestDto 회원이 수정한 여행후기 정보
     * @param thumbnail 여행후기 게시글 썸네일 이미지
     * @param imageList 여행후기 게시글 추가 이미지
     * @param email 수정 요청한 회원 이메일
     * @return 수정된 여행후기 게시글 id
     */
    @Transactional
    public TripDto.Id updateTrip(Long tripId, TripDto.Update requestDto, MultipartFile thumbnail,
                                 List<MultipartFile> imageList, String email) {
        Trip trip = getTrip(tripId);

        if (!trip.getMember().getEmail().equals(email))
            throw new CustomException(ErrorCode.POST_UPDATE_NOT_PERMISSION);

        //해쉬태그 수정
        tripHashtagRepository.deleteByTrip(trip);
        requestDto.getHashTag().forEach(hashtagTitle -> tripHashtagRepository.save(new TripHashtag(hashtagTitle, trip)));

        //이미지 수정
        tripImageService.deleteTripImage(trip);
        String thumbnailUrl = tripImageService.createTripImage(thumbnail, imageList, trip);

        //여행정보 수정
        trip.update(requestDto);

        //ES 수정
        TripSearchDoc tripSearchDoc = tripSearchRepository.findByTripId(tripId);
        tripSearchDoc.update(trip, requestDto.getHashTag(), thumbnailUrl);
        tripSearchRepository.save(tripSearchDoc);

        return new TripDto.Id(trip.getId());
    }

    /**
     * 여행후기 게시글 삭제
     * @param tripId 삭제하고자 하는 게시글 id
     * @param email 삭제 요청한 회원 이메일
     */
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

    /**
     * 조회수가 가장 많은 여행후기 게시글 목록 조회
     * @param size 노출되는 게시글 수
     * @return `size` 개수 만큼의 여행후기 게시글 목록
     */
    public List<TripDto.Top10> getRankByViewCount(long size){
        Set<String> ranks = redisTemplate.opsForZSet()
                .reverseRange(VIEW_RANK,0L,size+5L);

        if (ranks == null)
            return new ArrayList<>();

        List<TripDto.Top10> result =  tripSearchService.getRankByViewCount(ranks.stream()
                .map(Long::parseLong)
                .toList());

        return result.size() >= 10 ? result.subList(0, 10) : result;
    }

    /**
     * DB에 있는 여행후기 게시글의 해시태그 목록 조회
     * @param trip 조회하고자 하는 여행후기
     * @return 여행후기 게시글과 연관관계인 해시태그 목록
     */
    private List<TripHashtag> getHashtags(Trip trip){
        return tripHashtagRepository.findAllByTrip(trip);
    }

    /**
     * DB에 있는 여행후기 정보 조회
     * @param tripId 조회하고자 하는 여행후기 id
     * @return 조회된 여행후기 정보
     */
    private Trip getTrip(Long tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }
}