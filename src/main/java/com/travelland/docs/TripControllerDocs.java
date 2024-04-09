package com.travelland.docs;

import com.travelland.dto.TripDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "여행 정보 API", description = "여행정보 관련 API 명세서입니다.")
public interface TripControllerDocs {

    @Operation(summary = "여행정보 등록", description = "작성한 여행정보를 등록하는 API")
    ResponseEntity createTrip(@RequestPart TripDto.Create requestDto,
                              @RequestPart MultipartFile thumbnail,
                              @RequestPart(required = false) List<MultipartFile> imageList,
                              @RequestParam String email);

    @Operation(summary = "여행정보 상세조회", description = "선택한 여행정보에 대한 내용을 조회하는 API")
    ResponseEntity getTrip(@PathVariable Long tripId);

    @Operation(summary = "여행정보 목록 조회", description = "등록되어 있는 여행정보 목록을 페이지별로 조회하는 API")
    ResponseEntity getTripList(@RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "20") int size,
                               @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                               @RequestParam(required = false, defaultValue = "false") boolean isAsc);

    @Operation(summary = "여행정보 수정", description = "작성한 여행정보에 대한 내용을 수정하는 API")
    ResponseEntity updateTrip(@PathVariable Long tripId,
                              @RequestPart TripDto.Update requestDto,
                              @RequestPart MultipartFile thumbnail,
                              @RequestPart(required = false) List<MultipartFile> imageList,
                              @RequestParam String email);

    @Operation(summary = "여행정보 삭제", description = "등록한 여행정보를 삭제하는 API")
    ResponseEntity deleteTrip(@PathVariable Long tripId);

    @Operation(summary = "여행정보 좋아요 등록", description = "선택한 여행정보 게시글 좋아요를 등록하는 API")
    ResponseEntity createTripLike(@PathVariable Long tripId);

    @Operation(summary = "여행정보 좋아요 취소", description = "선택한 여행정보 게시글의 좋아요를 취소하는 API")
    ResponseEntity deleteTripLike(@PathVariable Long tripId);

    @Operation(summary = "여행정보 좋아요 목록 조회", description = "좋아요을 누른 여행정보 게시글 목록을 페이지별로 조회하는 API")
    ResponseEntity getTripLikeList(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "여행정보 스크랩 추가", description = "선택한 여행정보 게시글을 스크랩에 추가하는 API")
    ResponseEntity createTripScrap(@PathVariable Long tripId);

    @Operation(summary = "여행정보 스크랩 취소", description = "선택한 여행정보 게시글을 스크랩에서 삭제하는 API")
    ResponseEntity deleteTripScrap(@PathVariable Long tripId);

    @Operation(summary = "여행정보 스크랩 목록 조회", description = "스크랩한 여행정보 게시글 목록을 페이지별로 조회하는 API")
    ResponseEntity getTripScrapList(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "내가 작성한 여행정보 목록 조회", description = "로그인한 회원이 작성한 여행정보 게시글 목록을 페이지별로 조회하는 API")
    ResponseEntity getMyTripList(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "여행정보 해쉬태그 검색", description = "입력한 해쉬태그가 포함된 여행정보 게시글 목록을 페이지별로 조회하는 API")
    ResponseEntity searchTripByHashtag(@RequestParam String hashtag,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "20") int size,
                                       @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                       @RequestParam(required = false, defaultValue = "false") boolean isAsc);

    @Operation(summary = "인기 해쉬태그 TOP 5", description = "일일 검색량 상위 5개를 보여주는 API")
    ResponseEntity<List<TripDto.Rank>> getRecentTop5Keywords() throws IOException;
}
