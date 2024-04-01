package com.travelland.controller;

import com.travelland.docs.TripControllerDocs;
import com.travelland.dto.TripDto;
import com.travelland.service.TripLikeService;
import com.travelland.service.TripScrapService;
import com.travelland.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1/trips")
@RequiredArgsConstructor
public class TripController implements TripControllerDocs {

    private final TripService tripService;
    private final TripLikeService tripLikeService;
    private final TripScrapService tripScrapService;

    //여행정보 작성
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<TripDto.CreateResponse> createTrip(@RequestPart TripDto.CreateRequest requestDto,
                                                             @RequestPart List<MultipartFile> imageList,
                                                             @RequestPart String email) {
        TripDto.CreateResponse responseDto = tripService.createTrip(requestDto, imageList, email);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //여행정보 상세 조회
    @GetMapping("/{tripId}")
    public ResponseEntity<TripDto.GetResponse> getTrip(@PathVariable Long tripId) {
        TripDto.GetResponse responseDto = tripService.getTrip(tripId);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //여행정보 목록 조회
    @GetMapping
    public ResponseEntity<List<TripDto.GetListResponse>> getTripList(@RequestParam(defaultValue = "1") int page,
                                                                     @RequestParam(defaultValue = "20") int size,
                                                                     @RequestParam(required = false, defaultValue = "createdAt") String sort,
                                                                     @RequestParam(required = false, defaultValue = "false") boolean ASC) {
        List<TripDto.GetListResponse> responseDto = tripService.getTripList(page, size, sort, ASC);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //여행정보 수정
    @PutMapping(value = "/{tripId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<TripDto.UpdateResponse> updateTrip(@PathVariable Long tripId,
                                                             @RequestPart TripDto.UpdateRequest requestDto,
                                                             @RequestPart List<MultipartFile> imageList,
                                                             @RequestPart String email){
        TripDto.UpdateResponse responseDto = tripService.updateTrip(tripId, requestDto, imageList, email);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //여행정보 삭제
    @DeleteMapping("{tripId}")
    public ResponseEntity<TripDto.DeleteResponse> deleteTrip(@PathVariable Long tripId) {
        tripService.deleteTrip(tripId, "test@email.com");

        return ResponseEntity.status(HttpStatus.OK).body(new TripDto.DeleteResponse(true));
    }

    //여행정보 좋아요 등록
    @PostMapping("/{tripId}/like")
    public ResponseEntity<TripDto.TripLikeResponse> createTripLike(@PathVariable Long tripId) {
        tripLikeService.createTripLike(tripId, "test@email.com");

        return ResponseEntity.status(HttpStatus.OK).body(new TripDto.TripLikeResponse(true));
    }

    //여행정보 좋아요 취소
    @DeleteMapping("/{tripId}/like")
    public ResponseEntity<TripDto.TripLikeResponse> deleteTripLike(@PathVariable Long tripId) {
        tripLikeService.deleteTripLike(tripId, "test@email.com");

        return ResponseEntity.status(HttpStatus.OK).body(new TripDto.TripLikeResponse(false));
    }

    //여행정보 좋아요 목록 조회
    @GetMapping("/like")
    public ResponseEntity<List<TripDto.GetTripLikeListResponse>> getTripLikeList(@RequestParam(defaultValue = "1") int page,
                                                                                 @RequestParam(defaultValue = "20") int size,
                                                                                 @RequestParam(required = false, defaultValue = "createdAt") String sort,
                                                                                 @RequestParam(required = false, defaultValue = "false") boolean ASC) {
        List<TripDto.GetTripLikeListResponse> responseDto = tripLikeService.getTripLikeList(page, size, sort, ASC, "test@email.com");

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //여행정보 스크랩 추가
    @PostMapping("/{tripId}/scrap")
    public ResponseEntity<TripDto.TripScrapResponse> createTripScrap(@PathVariable Long tripId) {
        tripScrapService.createTripScrap(tripId, "test@email.com");

        return ResponseEntity.status(HttpStatus.OK).body(new TripDto.TripScrapResponse(true));
    }

    //여행정보 스크랩 취소
    @DeleteMapping("/{tripId}/scrap")
    public ResponseEntity<TripDto.TripScrapResponse> deleteTripScrap(@PathVariable Long tripId) {
        tripScrapService.deleteTripScrap(tripId, "test@email.com");

        return ResponseEntity.status(HttpStatus.OK).body(new TripDto.TripScrapResponse(false));
    }

    //여행정보 스크랩 목록 조회
    @GetMapping("/scrap")
    public ResponseEntity<List<TripDto.GetTripScrapListResponse>> getTripScrapList(@RequestParam(defaultValue = "1") int page,
                                                                                   @RequestParam(defaultValue = "20") int size,
                                                                                   @RequestParam(required = false, defaultValue = "createdAt") String sort,
                                                                                   @RequestParam(required = false, defaultValue = "false") boolean ASC) {
        List<TripDto.GetTripScrapListResponse> responseDto = tripScrapService.getTripScrapList(page, size, sort, ASC, "test@email.com");

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //여행정보 해쉬태그 검색

}
