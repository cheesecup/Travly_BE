package com.travelland.controller;

import com.travelland.domain.member.Member;
import com.travelland.dto.trip.TripCommentDto;
import com.travelland.dto.trip.TripDto;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.global.security.UserDetailsImpl;
import com.travelland.repository.member.MemberRepository;
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
    private final MemberRepository memberRepository;

    @PostMapping(value = "/trips", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<TripDto.Id> createTrip(@Validated(TripValidationSequence.class) @RequestPart TripDto.Create requestDto,
                                                 @RequestPart MultipartFile thumbnail,
                                                 @RequestPart(required = false) List<MultipartFile> imageList,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        TripDto.Id responseDto = tripService.createTrip(requestDto, thumbnail, imageList, testMember());

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/trips/{tripId}")
    public ResponseEntity<TripDto.Get> getTrip(@PathVariable Long tripId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        String email = "";
//        if (userDetails != null) email = userDetails.getUsername();

        TripDto.Get responseDto = tripService.getTrip(tripId, testMember().getEmail());

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/trips")
    public ResponseEntity<List<TripDto.GetList>> getTripList(@RequestParam(defaultValue = "1") int page,
                                                             @RequestParam(defaultValue = "9") int size,
                                                             @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                                             @RequestParam(required = false, defaultValue = "false") boolean isAsc) {
        return ResponseEntity.status(HttpStatus.OK).body(tripSearchService.getTripList(page, size, sortBy, isAsc));
    }

    @PutMapping(value = "/trips/{tripId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<TripDto.Id> updateTrip(@PathVariable Long tripId,
                                                 @Validated(TripValidationSequence.class) @RequestPart TripDto.Update requestDto,
                                                 @RequestPart MultipartFile thumbnail,
                                                 @RequestPart(required = false) List<MultipartFile> imageList,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        TripDto.Id responseDto = tripService.updateTrip(tripId, requestDto, thumbnail, imageList, testMember());

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/trips/{tripId}")
    public ResponseEntity<TripDto.Delete> deleteTrip(@PathVariable Long tripId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        tripService.deleteTrip(tripId, testMember());

        return ResponseEntity.status(HttpStatus.OK).body(new TripDto.Delete(true));
    }

    @PostMapping("/trips/{tripId}/like")
    public ResponseEntity<TripDto.Result> createTripLike(@PathVariable Long tripId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        checkLogin(userDetails);
        tripLikeService.registerTripLike(tripId, userDetails.getMember());

        return ResponseEntity.status(HttpStatus.OK).body(new TripDto.Result(true));
    }

    @DeleteMapping("/trips/{tripId}/like")
    public ResponseEntity<TripDto.Result> deleteTripLike(@PathVariable Long tripId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        checkLogin(userDetails);
        tripLikeService.cancelTripLike(tripId, userDetails.getMember());

        return ResponseEntity.status(HttpStatus.OK).body(new TripDto.Result(false));
    }

    @GetMapping("/trips/like")
    public ResponseEntity<List<TripDto.Likes>> getTripLikeList(@RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "9") int size,
                                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<TripDto.Likes> responseDto = tripLikeService.getTripLikeList(page, size, userDetails.getMember());

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/trips/{tripId}/scrap")
    public ResponseEntity<TripDto.Result> createTripScrap(@PathVariable Long tripId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        checkLogin(userDetails);
        tripScrapService.registerTripScrap(tripId, userDetails.getMember());

        return ResponseEntity.status(HttpStatus.OK).body(new TripDto.Result(true));
    }

    @DeleteMapping("/trips/{tripId}/scrap")
    public ResponseEntity<TripDto.Result> deleteTripScrap(@PathVariable Long tripId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        checkLogin(userDetails);
        tripScrapService.cancelTripScrap(tripId, userDetails.getMember());

        return ResponseEntity.status(HttpStatus.OK).body(new TripDto.Result(false));
    }

    @GetMapping("/trips/scrap")
    public ResponseEntity<List<TripDto.Scraps>> getTripScrapList(@RequestParam(defaultValue = "1") int page,
                                                                 @RequestParam(defaultValue = "9") int size,
                                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<TripDto.Scraps> responseDto = tripScrapService.getTripScrapList(page, size, userDetails.getMember());

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/users/trips")
    public ResponseEntity<List<TripDto.GetList>> getMyTripList(@RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "9") int size,
                                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        checkLogin(userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(tripSearchService.getMyTripList(page, size, userDetails.getUsername()));
    }

    @GetMapping("/trips/search")
    public ResponseEntity<TripDto.SearchResult> searchTrip(@RequestParam String text,
                                                                  @RequestParam(defaultValue = "1") int page,
                                                                  @RequestParam(defaultValue = "9") int size,
                                                                  @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                                                  @RequestParam(required = false, defaultValue = "false") Boolean isAsc) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(tripSearchService.totalSearchTrip(text, page, size, sortBy, isAsc));
    }

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
                .body(tripSearchService.searchTripByArea(area, page, size, sortBy, isAsc));
    }

    @GetMapping("/trips/rank/hashtag")
    public ResponseEntity<List<String>> getRecentTopHashtag(){
        return ResponseEntity.status(HttpStatus.OK).body(tripSearchService.getRecentlyTopSearch("hashtag"));
    }

    @GetMapping("/trips/rank/area")
    public ResponseEntity<List<String>> getRecentTopArea(){
        return ResponseEntity.status(HttpStatus.OK).body(tripSearchService.getRecentlyTopSearch("area"));
    }

    @PostMapping("/trips/{tripId}/comments")
    public ResponseEntity<TripCommentDto.Id> createTripComment(@PathVariable Long tripId,
                                                               @RequestBody TripCommentDto.Create requestDto,
                                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        TripCommentDto.Id responseDto = tripCommentService.createTripComment(requestDto, tripId, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/trips/{tripId}/comments")
    public ResponseEntity<List<TripCommentDto.GetList>> getTripCommentList(@PathVariable Long tripId,
                                                                           @RequestParam(defaultValue = "1") int page,
                                                                           @RequestParam(defaultValue = "9") int size) {
        List<TripCommentDto.GetList> responseDto = tripCommentService.getTripCommentList(tripId, page, size);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/trips/{tripId}/comments/{commentId}")
    public ResponseEntity<TripCommentDto.Id> updateTripComment(@PathVariable Long tripId,
                                                               @PathVariable Long commentId,
                                                               @RequestBody TripCommentDto.Update requestDto,
                                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        TripCommentDto.Id responseDto = tripCommentService.updateTripComment(tripId, commentId, requestDto, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/trips/{tripId}/comments/{commentId}")
    public ResponseEntity<TripCommentDto.Delete> deleteTripComment(@PathVariable Long tripId,
                                                                   @PathVariable Long commentId,
                                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        tripCommentService.deleteTripComment(tripId, commentId, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(new TripCommentDto.Delete(true));
    }

    @GetMapping("/trips/rank")
    public ResponseEntity<List<TripDto.Top10>> getTripListTop10() {
        return ResponseEntity.status(HttpStatus.OK).body(tripService.getRankByViewCount(10L));
    }

    @GetMapping("/trips/random")
    public ResponseEntity<List<TripDto.GetList>> getTripListRandom8(){
        return ResponseEntity.status(HttpStatus.OK).body(tripSearchService.getRandomTrip());
    }

    @GetMapping("/trips/{tripId}/recommend")
    public ResponseEntity<List<TripDto.GetList>> getTripRecommend(@PathVariable Long tripId){
        return ResponseEntity.status(HttpStatus.OK).body(tripSearchService.getRecommendTrip(tripId));
    }

    @GetMapping("/trips/sync/es")
    public ResponseEntity<Boolean> syncDBtoES(){
        tripSearchService.syncDBtoES();
        return ResponseEntity.status(HttpStatus.OK).body(true);
    }

    private void checkLogin(UserDetailsImpl userDetails) {
        if (userDetails == null) throw new CustomException(ErrorCode.STATUS_NOT_LOGIN);
    }

    private Member testMember() {
        return memberRepository.findByEmail("test@test.com").orElseThrow(IllegalArgumentException::new);
    }
}
