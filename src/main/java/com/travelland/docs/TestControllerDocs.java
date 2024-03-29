package com.travelland.docs;

import com.travelland.dto.MemberRequestDto.LoginRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Swagger Example", description = "스웨거 작성 예시 API")
public interface TestControllerDocs {

    @Operation(summary = "스웨거 테스트", description = "스웨거 테스트 예시 API")
    ResponseEntity<String> swaggerTest(@RequestBody LoginRequestDto requestDto);

    @Operation(summary = "test method", description = "test method API")
    ResponseEntity<String> test();

    @Operation(summary = "SwaggerTest", description = "swagger Test API")
    ResponseEntity postRequest(@RequestParam String name);

}
