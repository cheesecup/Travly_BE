package com.travelland.global.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.travelland.dto.trip.TripImageDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE, connection = EmbeddedDatabaseConnection.H2)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class S3FileServiceTest {

    @Autowired S3FileService s3FileService;
    @Autowired AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private static final String FILE_PATH = "src/test/resources/static/img/test.jpeg";

    MockMultipartFile createThumbnail() throws IOException {
        return new MockMultipartFile("썸네일 이미지", "thumbnail.png", MediaType.IMAGE_PNG_VALUE, new FileInputStream(FILE_PATH));
    }

    MockMultipartFile createMultipartFile() {
        return new MockMultipartFile("추가 이미지", "other.png", MediaType.IMAGE_PNG_VALUE, "other".getBytes());
    }

    @Test
    @DisplayName("S3 원본 이미지 업로드 테스트 - 성공")
    void save_original_image_test() {
        // given
        MultipartFile image = createMultipartFile();

        // when
        TripImageDto.CreateRequest createRequest = s3FileService.saveOriginalImage(image);

        // then
        String imgUrl = amazonS3.getUrl(bucket, createRequest.getStoreImageName()).toString();
        assertEquals(createRequest.getImageUrl(), imgUrl);

    }

    @Test
    @DisplayName("S3 이미지 삭제 테스트 - 성공")
    void delete_file_test() {
        // given
        MultipartFile image = createMultipartFile();
        TripImageDto.CreateRequest createRequest = s3FileService.saveOriginalImage(image);

        // when
        s3FileService.deleteFile(createRequest.getStoreImageName());

    }

    @Test
    @DisplayName("S3 리사이즈 이미지 업로드 테스트 - 성공")
    void save_resize_image_test() throws IOException {
        // given
        MultipartFile thumbnail = createThumbnail();

        // when
        TripImageDto.CreateRequest createRequest = s3FileService.saveResizeImage(thumbnail);

        // then
        String imgUrl = amazonS3.getUrl(bucket, createRequest.getStoreImageName()).toString();
        assertEquals(createRequest.getImageUrl(), imgUrl);

    }

}