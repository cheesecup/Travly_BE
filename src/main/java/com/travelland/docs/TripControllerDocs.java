package com.travelland.docs;

import com.travelland.dto.TripDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "여행 정보 API", description = "여행정보 관련 API 명세서입니다.")
public interface TripControllerDocs {

    @Operation(summary = "여행정보 등록", description = "작성한 여행정보를 등록하는 API")
    ResponseEntity createTrip(@RequestPart TripDto.CreateRequest requestDto,
                              @RequestPart List<MultipartFile> imageList,
                              @RequestPart String email);

    @Operation(summary = "여행정보 상세조회", description = "선택한 여행정보에 대한 내용을 조회하는 API")
    ResponseEntity getTrip(@PathVariable Long tripId);

    @Operation(summary = "여행정보 목록 조회", description = "등록되어 있는 여행정보 목록을 페이지별로 조회하는 API")
    ResponseEntity getTripList(@RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "5") int size,
                               @RequestParam(required = false, defaultValue = "createdAt") String sort,
                               @RequestParam(required = false, defaultValue = "false") boolean ASC);

    @Operation(summary = "여행정보 수정", description = "작성한 여행정보에 대한 내용을 수정하는 API")
    ResponseEntity updateTrip(@PathVariable Long tripId,
                              @RequestPart TripDto.UpdateRequest requestDto,
                              @RequestPart List<MultipartFile> imageList,
                              @RequestPart String email);

    @Operation(summary = "여행정보 삭제", description = "등록한 여행정보를 삭제하는 API")
    ResponseEntity deleteTrip(@PathVariable Long tripId);
}
