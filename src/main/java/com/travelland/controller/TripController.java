package com.travelland.controller;

import com.travelland.docs.TripControllerDocs;
import com.travelland.dto.TripDto;
import com.travelland.service.trip.TripLikeService;
import com.travelland.service.trip.TripScrapService;
import com.travelland.service.trip.TripSearchService;
import com.travelland.service.trip.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class TripController implements TripControllerDocs {

    private final TripService tripService;
    private final TripLikeService tripLikeService;
    private final TripScrapService tripScrapService;
    private final TripSearchService tripSearchService;

    //여행정보 작성
    @PostMapping(value = "/trips", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<TripDto.Id> createTrip(@RequestPart TripDto.Create requestDto,
                                                 @RequestPart MultipartFile thumbnail,
                                                 @RequestPart(required = false) List<MultipartFile> imageList,
                                                 @RequestParam String email) {
        TripDto.Id responseDto = tripService.createTrip(requestDto, thumbnail, imageList, email);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //여행정보 상세 조회
    @GetMapping("/trips/{tripId}")
    public ResponseEntity<TripDto.Get> getTrip(@PathVariable Long tripId) {
        TripDto.Get responseDto = tripService.getTrip(tripId);

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
                                                 @RequestPart TripDto.Update requestDto,
                                                 @RequestPart(required = false) MultipartFile thumbnail,
                                                 @RequestPart(required = false) List<MultipartFile> imageList,
                                                 @RequestParam String email){
        TripDto.Id responseDto = tripService.updateTrip(tripId, requestDto, thumbnail, imageList, email);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //여행정보 삭제
    @DeleteMapping("/trips/{tripId}")
    public ResponseEntity<TripDto.Delete> deleteTrip(@PathVariable Long tripId) {
        tripService.deleteTrip(tripId, "test@test.com");

        return ResponseEntity.status(HttpStatus.OK).body(new TripDto.Delete(true));
    }

    //여행정보 좋아요 등록
    @PostMapping("/trips/{tripId}/like")
    public ResponseEntity<TripDto.Result> createTripLike(@PathVariable Long tripId) {
        tripLikeService.registerTripLike(tripId, "test@test.com");

        return ResponseEntity.status(HttpStatus.OK).body(new TripDto.Result(true));
    }

    //여행정보 좋아요 취소
    @DeleteMapping("/trips/{tripId}/like")
    public ResponseEntity<TripDto.Result> deleteTripLike(@PathVariable Long tripId) {
        tripLikeService.cancelTripLike(tripId, "test@test.com");

        return ResponseEntity.status(HttpStatus.OK).body(new TripDto.Result(false));
    }

    //여행정보 좋아요 목록 조회
    @GetMapping("/trips/like")
    public ResponseEntity<List<TripDto.Likes>> getTripLikeList(@RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "9") int size) {
        List<TripDto.Likes> responseDto = tripLikeService.getTripLikeList(page, size, "test@test.com");

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //여행정보 스크랩 등록
    @PostMapping("/trips/{tripId}/scrap")
    public ResponseEntity<TripDto.Result> createTripScrap(@PathVariable Long tripId) {
        tripScrapService.registerTripScrap(tripId, "test@test.com");

        return ResponseEntity.status(HttpStatus.OK).body(new TripDto.Result(true));
    }

    //여행정보 스크랩 취소
    @DeleteMapping("/trips/{tripId}/scrap")
    public ResponseEntity<TripDto.Result> deleteTripScrap(@PathVariable Long tripId) {
        tripScrapService.cancelTripScrap(tripId, "test@test.com");

        return ResponseEntity.status(HttpStatus.OK).body(new TripDto.Result(false));
    }

    //여행정보 스크랩 목록 조회
    @GetMapping("/trips/scrap")
    public ResponseEntity<List<TripDto.Scraps>> getTripScrapList(@RequestParam(defaultValue = "1") int page,
                                                                 @RequestParam(defaultValue = "9") int size) {
        List<TripDto.Scraps> responseDto = tripScrapService.getTripScrapList(page, size, "test@test.com");

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    // 작성한 여행정보 게시글 목록 조회
    @GetMapping("/users/trips")
    public ResponseEntity<List<TripDto.GetList>> getMyTripList(@RequestParam(defaultValue = "1") int page,
                                                                   @RequestParam(defaultValue = "9") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(tripSearchService.getMyTripList(page, size, "test@test.com"));
    }

    //여행정보 해쉬태그 검색
    @GetMapping("/trips/hashtag")
    public ResponseEntity<TripDto.SearchResult> searchTripByHashtag(@RequestParam String hashtag,
                                                                    @RequestParam(defaultValue = "1") int page,
                                                                    @RequestParam(defaultValue = "9") int size,
                                                                    @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                                                    @RequestParam(required = false, defaultValue = "false") boolean isAsc) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(tripSearchService.searchTripByHashtag(hashtag, page, size, sortBy, isAsc));
    }

    @GetMapping("/trips/rank/hashtag")
    public ResponseEntity<List<TripDto.Rank>> getRecentTop5Keywords() throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body(tripSearchService.getRecentlyTopSearch());
    }
}
