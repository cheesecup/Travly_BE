package com.travelland.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.travelland.dto.TripImageDto.CreateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3FileService {

    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // S3에 이미지 업로드
    public CreateRequest s3Upload(MultipartFile multipartFile) {
        String oriImgName = multipartFile.getOriginalFilename();
        String storeImageName = uuidImageName(oriImgName);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        try(InputStream inputStream = multipartFile.getInputStream()) {
            s3Client.putObject(new PutObjectRequest(bucket, storeImageName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            log.error("이미지 업로드 실패");
        }

        String imageUrl = s3Client.getUrl(bucket, storeImageName).toString();

        return new CreateRequest(imageUrl, storeImageName);
    }

    // 저장된 이미지 퍄일 삭제
    @Async
    public void deleteFile(String storeImageName) {
        s3Client.deleteObject(new DeleteObjectRequest(bucket, storeImageName));
    }

    //이미지파일 원본이름 랜덤 변경
    private String uuidImageName(String oriImgName) {
        return UUID.randomUUID().toString().concat(getFileExtension(oriImgName));
    }

    //이미지파일 원본이름 확장자 자르기
    private String getFileExtension(String oriImgName) {
        try {
            return oriImgName.substring(oriImgName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
