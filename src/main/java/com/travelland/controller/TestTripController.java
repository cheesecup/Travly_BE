package com.travelland.controller;

import com.travelland.dto.trip.TripDto;
import com.travelland.service.trip.TestTripService;
import com.travelland.swagger.TestTripControllerDocs;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestTripController implements TestTripControllerDocs {

    private final TestTripService testTripService;

    //스크랩한 여행 목록 조회
    @GetMapping("/trips/scrap")
    public ResponseEntity<TripDto.GetMyScraps> getTripScrapList(@RequestParam(defaultValue = "1") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {
        TripDto.GetMyScraps responseDto = testTripService.getTripScrapListTEST(page, size);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //작성한 여행정보 게시글 목록 조회
    @GetMapping("/users/trips")
    public ResponseEntity<TripDto.GetMyList> getMyTripList(@RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        TripDto.GetMyList responseDto = testTripService.getMyTripListTEST(page, size);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
