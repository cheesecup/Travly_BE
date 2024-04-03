package com.travelland.controller;

import com.travelland.document.TripSearchDoc;
import com.travelland.dto.TripSearchDto;
import com.travelland.service.trip.TripSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1/trips/search")
@RequiredArgsConstructor
public class TripSearchController {
    private final TripSearchService tripSearchService;

    @PostMapping
    public ResponseEntity<TripSearchDto.GetResponse> createTrip(@RequestBody TripSearchDto.CreateRequest requestDto) {
        TripSearchDto.GetResponse responseDto = tripSearchService.createTripDocument(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
    //여행정보 상세 조회
    @GetMapping("/{tripId}")
    public ResponseEntity<TripSearchDto.GetResponse> getTrip(@PathVariable Long tripId) {
        TripSearchDto.GetResponse responseDto = tripSearchService.searchTripById(tripId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<TripSearchDoc> getTripByHashtag(@RequestParam String hashtag) {
        Page<TripSearchDoc> tripDocuments =  tripSearchService.searchTripByHashtag(hashtag);
        return ResponseEntity.status(HttpStatus.OK).body(tripDocuments.getContent().get(0));
    }

    @GetMapping("/top5")
    public ResponseEntity<List<TripSearchDto.RankResponse>> getRecentTop5Keywords() throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body(tripSearchService.getPopwordList());
    }



}
