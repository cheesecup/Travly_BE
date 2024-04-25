package com.travelland.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "개발 테스트용 API", description = "기능 테스트를 위한 API 명세서입니다. 배포 API와 다릅니다")
public interface TestTripControllerDocs {

    @Operation(summary = "내가 스크랩한 여행후기 게시글 목록 조회", description = "스크랩한 여행후기에 대한 목록을 조회하는 테스트 API")
    ResponseEntity getTripScrapList(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "9") int size);

    @Operation(summary = "내가 작성한 여행후기 게시글 목록 조회", description = "내가 작성한 여행후기에 대한 목록을 조회하는 테스트 API")
    ResponseEntity getMyTripList(@RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "9") int size);

}
