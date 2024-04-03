package com.travelland.controller;

import com.travelland.document.TripDocument;
import com.travelland.dto.TripSearchDto;
import com.travelland.service.TripDocumentService;
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
    private final TripDocumentService tripDocumentService;

    @PostMapping
    public ResponseEntity<TripSearchDto.GetResponse> createTrip(@RequestBody TripSearchDto.CreateRequest requestDto) {
        TripSearchDto.GetResponse responseDto = tripDocumentService.createTripDocument(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
    //여행정보 상세 조회
    @GetMapping("/{tripId}")
    public ResponseEntity<TripSearchDto.GetResponse> getTrip(@PathVariable Long tripId) {
        TripSearchDto.GetResponse responseDto = tripDocumentService.searchTripById(tripId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<TripDocument> getTripByHashtag(@RequestParam String hashtag) {
        Page<TripDocument> tripDocuments =  tripDocumentService.searchTripByHashtag(hashtag);
        return ResponseEntity.status(HttpStatus.OK).body(tripDocuments.getContent().get(0));
    }

    @GetMapping("/top5")
    public ResponseEntity<List<TripSearchDto.RankResponse>> getRecentTop5Keywords() throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body(tripDocumentService.getPopwordList());
    }



}
