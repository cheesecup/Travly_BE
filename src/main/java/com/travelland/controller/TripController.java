package com.travelland.controller;

import com.travelland.docs.TripControllerDocs;
import com.travelland.dto.TripDto;
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
                                      @RequestParam(defaultValue = "5") int size,
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

    //여행정보 좋아요 취소

    //여행정보 좋아요 목록 조회

    //여행정보 스크랩 추가

    //여행정보 스크랩 취소

    //여행정보 스크랩 목록 조회

    //여행정보 해쉬태그 검색

}
