package com.travelland.controller;

import com.travelland.docs.TripControllerDocs;
import com.travelland.dto.TripDto.*;
import com.travelland.service.TripService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * TODO
 *  - 로그인 적용 후 `mockEmail` 변수 지우기
 *    - .yml 설정에서 `mockEmail` 설정 지우기
 *    - `UserDetailsImpl` 적용하기
 */
@Slf4j
@RestController
@RequestMapping("/v1/trips")
@RequiredArgsConstructor
public class TripController implements TripControllerDocs {

    private final TripService tripService;

    @Value("${mock.email}")
    private String email;

    //여행정보 작성
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity createTrip(@RequestPart CreateRequest requestDto,
                                     @RequestPart List<MultipartFile> imageList
//                                     @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        CreateResponse responseDto = tripService.createTrip(requestDto, imageList, email);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //여행정보 상세 조회
    @GetMapping("/{tripId}")
    public ResponseEntity getTrip(@PathVariable Long tripId) {
        GetResponse responseDto = tripService.getTrip(tripId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //여행정보 목록 조회
    @GetMapping
    public ResponseEntity getTripList(@RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "5") int size,
                                      @RequestParam(required = false, defaultValue = "createdAt") String sort,
                                      @RequestParam(required = false, defaultValue = "false") boolean ASC) {
        List<GetListResponse> responseDto = tripService.getTripList(page, size, sort, ASC);
        return ResponseEntity.ok(responseDto);
    }

    //여행정보 수정
    @PutMapping(value = "/{tripId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity updateTrip(@PathVariable Long tripId,
                                     @RequestPart UpdateRequest requestDto,
                                     @RequestPart List<MultipartFile> imageList
//                                     @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        UpdateResponse responseDto = tripService.updateTrip(tripId, requestDto, imageList, email);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //여행정보 삭제
    @DeleteMapping("{tripId}")
    public ResponseEntity deleteTrip(@PathVariable Long tripId
//                                     @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        tripService.deleteTrip(tripId, email);

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    //여행정보 좋아요 등록

    //여행정보 좋아요 취소

    //여행정보 좋아요 목록 조회

    //여행정보 스크랩 추가

    //여행정보 스크랩 취소

    //여행정보 스크랩 목록 조회

    //여행정보 해쉬태그 검색


}
