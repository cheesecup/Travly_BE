package com.travelland.controller;

import com.travelland.dto.trip.TripCommentDto;
import com.travelland.dto.trip.TripDto;
import com.travelland.global.security.UserDetailsImpl;
import com.travelland.service.trip.*;
import com.travelland.swagger.TripControllerDocs;
import com.travelland.valid.trip.TripValidationSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class TripController implements TripControllerDocs {

    private final TripService tripService;
    private final TripLikeService tripLikeService;
    private final TripScrapService tripScrapService;
    private final TripSearchService tripSearchService;
    private final TripCommentService tripCommentService;

    //여행정보 작성
    @PostMapping(value = "/trips", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<TripDto.Id> createTrip(@Validated(TripValidationSequence.class) @RequestPart TripDto.Create requestDto,
                                                 @RequestPart MultipartFile thumbnail,
                                                 @RequestPart(required = false) List<MultipartFile> imageList,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        TripDto.Id responseDto = tripService.createTrip(requestDto, thumbnail, imageList, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //여행정보 상세 조회
    @GetMapping("/trips/{tripId}")
    public ResponseEntity<TripDto.Get> getTrip(@PathVariable Long tripId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String email = "";
        if (userDetails != null) email = userDetails.getUsername();

        TripDto.Get responseDto = tripService.getTrip(tripId, email);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //여행정보 목록 조회
    @GetMapping("/trips")
    public ResponseEntity<List<TripDto.GetList>> getTripList(@RequestParam(defaultValue = "1") int page,
                                                             @RequestParam(defaultValue = "9") int size,
                                                             @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                                             @RequestParam(required = false, defaultValue = "false") boolean isAsc) {
        return ResponseEntity.status(HttpStatus.OK).body(tripSearchService.getTripList(page, size, sortBy, isAsc));
    }

    //여행정보 수정
    @PutMapping(value = "/trips/{tripId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<TripDto.Id> updateTrip(@PathVariable Long tripId,
                                                 @Validated(TripValidationSequence.class) @RequestPart TripDto.Update requestDto,
                                                 @RequestPart(required = false) MultipartFile thumbnail,
                                                 @RequestPart(required = false) List<MultipartFile> imageList,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        TripDto.Id responseDto = tripService.updateTrip(tripId, requestDto, thumbnail, imageList, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //여행정보 삭제
    @DeleteMapping("/trips/{tripId}")
    public ResponseEntity<TripDto.Delete> deleteTrip(@PathVariable Long tripId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        tripService.deleteTrip(tripId, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(new TripDto.Delete(true));
    }

    //여행정보 좋아요 등록
    @PostMapping("/trips/{tripId}/like")
    public ResponseEntity<TripDto.Result> createTripLike(@PathVariable Long tripId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        tripLikeService.registerTripLike(tripId, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(new TripDto.Result(true));
    }

    //여행정보 좋아요 취소
    @DeleteMapping("/trips/{tripId}/like")
    public ResponseEntity<TripDto.Result> deleteTripLike(@PathVariable Long tripId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        tripLikeService.cancelTripLike(tripId, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(new TripDto.Result(false));
    }

    //여행정보 좋아요 목록 조회
    @GetMapping("/trips/like")
    public ResponseEntity<List<TripDto.Likes>> getTripLikeList(@RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "9") int size,
                                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<TripDto.Likes> responseDto = tripLikeService.getTripLikeList(page, size, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //여행정보 스크랩 등록
    @PostMapping("/trips/{tripId}/scrap")
    public ResponseEntity<TripDto.Result> createTripScrap(@PathVariable Long tripId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        tripScrapService.registerTripScrap(tripId, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(new TripDto.Result(true));
    }

    //여행정보 스크랩 취소
    @DeleteMapping("/trips/{tripId}/scrap")
    public ResponseEntity<TripDto.Result> deleteTripScrap(@PathVariable Long tripId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        tripScrapService.cancelTripScrap(tripId, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(new TripDto.Result(false));
    }

    //스크랩한 여행 목록 조회
    @GetMapping("/trips/scrap")
    public ResponseEntity<List<TripDto.Scraps>> getTripScrapList(@RequestParam(defaultValue = "1") int page,
                                                                 @RequestParam(defaultValue = "9") int size,
                                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<TripDto.Scraps> responseDto = tripScrapService.getTripScrapList(page, size, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //작성한 여행정보 게시글 목록 조회
    @GetMapping("/users/trips")
    public ResponseEntity<List<TripDto.GetList>> getMyTripList(@RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "9") int size,
                                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(tripSearchService.getMyTripList(page, size, userDetails.getUsername()));
    }

    //여행정보 해쉬태그 검색
    @GetMapping("/trips/search")
    public ResponseEntity<TripDto.SearchResult> searchTrip(@RequestParam String text,
                                                                  @RequestParam(defaultValue = "1") int page,
                                                                  @RequestParam(defaultValue = "9") int size,
                                                                  @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                                                  @RequestParam(required = false, defaultValue = "false") Boolean isAsc) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(tripSearchService.searchTrip(text, page, size, sortBy, isAsc));
    }

    //여행정보 해쉬태그 검색
    @GetMapping("/trips/search/title")
    public ResponseEntity<TripDto.SearchResult> searchTripByTitle(@RequestParam String title,
                                                                    @RequestParam(defaultValue = "1") int page,
                                                                    @RequestParam(defaultValue = "9") int size,
                                                                    @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                                                    @RequestParam(required = false, defaultValue = "false") Boolean isAsc) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(tripSearchService.searchTripByTitle(title, page, size, sortBy, isAsc));
    }
    @GetMapping("/trips/search/hashtag")
    public ResponseEntity<TripDto.SearchResult> searchTripByHashtag(@RequestParam String hashtag,
                                                                    @RequestParam(defaultValue = "1") int page,
                                                                    @RequestParam(defaultValue = "9") int size,
                                                                    @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                                                    @RequestParam(required = false, defaultValue = "false") Boolean isAsc) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(tripSearchService.searchTripByField("hashtag", hashtag, page, size, sortBy, isAsc));
    }
    @GetMapping("/trips/search/area")
    public ResponseEntity<TripDto.SearchResult> searchTripByArea(@RequestParam String area,
                                                                    @RequestParam(defaultValue = "1") int page,
                                                                    @RequestParam(defaultValue = "9") int size,
                                                                    @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                                                    @RequestParam(required = false, defaultValue = "false") Boolean isAsc) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(tripSearchService.searchTripByField("area", area, page, size, sortBy, isAsc));
    }

    //여행정보 해쉬태그 검색량 상위 TOP5
    @GetMapping("/trips/rank/hashtag")
    public ResponseEntity<List<String>> getRecentTopHashtag(){
        return ResponseEntity.status(HttpStatus.OK).body(tripSearchService.getRecentlyTopSearch("hashtag"));
    }

    @GetMapping("/trips/rank/area")
    public ResponseEntity<List<String>> getRecentTopArea(){
        return ResponseEntity.status(HttpStatus.OK).body(tripSearchService.getRecentlyTopSearch("area"));
    }

    //여행정보 댓글 작성
    @PostMapping("/trips/{tripId}/comments")
    public ResponseEntity<TripCommentDto.Id> createTripComment(@PathVariable Long tripId,
                                                               @RequestBody TripCommentDto.Create requestDto,
                                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        TripCommentDto.Id responseDto = tripCommentService.createTripComment(requestDto, tripId, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    //여행정보 댓글 목록 조회
    @GetMapping("/trips/{tripId}/comments")
    public ResponseEntity<List<TripCommentDto.GetList>> getTripCommentList(@PathVariable Long tripId,
                                                                           @RequestParam(defaultValue = "1") int page,
                                                                           @RequestParam(defaultValue = "9") int size) {
        List<TripCommentDto.GetList> responseDto = tripCommentService.getTripCommentList(tripId, page, size);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //여행정보 댓글 수정
    @PutMapping("/trips/{tripId}/comments/{commentId}")
    public ResponseEntity<TripCommentDto.Id> updateTripComment(@PathVariable Long tripId,
                                                               @PathVariable Long commentId,
                                                               @RequestBody TripCommentDto.Update requestDto,
                                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        TripCommentDto.Id responseDto = tripCommentService.updateTripComment(tripId, commentId, requestDto, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //여행정보 댓글 삭제
    @DeleteMapping("/trips/{tripId}/comments/{commentId}")
    public ResponseEntity<TripCommentDto.Delete> deleteTripComment(@PathVariable Long tripId,
                                                                   @PathVariable Long commentId,
                                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        tripCommentService.deleteTripComment(tripId, commentId, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(new TripCommentDto.Delete(true));
    }

    //여행정보 조회수 TOP 10 목록 조회
    @GetMapping("/trips/rank")
    public ResponseEntity<List<TripDto.GetList>> getTripListTop10() {
        return ResponseEntity.status(HttpStatus.OK).body(tripService.getRankByViewCount(10L));
    }

    //지역 카테고리별 목록 조회

}
