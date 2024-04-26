package com.travelland.global.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.travelland.dto.trip.TripImageDto.CreateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j(topic = "S3 Upload / Delete Log")
@Service
@RequiredArgsConstructor
public class S3FileService {

    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    //원본 사이즈 이미지 업로드
    public CreateRequest saveOriginalImage(MultipartFile multipartFile) {
        String oriImgName = multipartFile.getOriginalFilename();
        String storeImageName = uuidImageName(oriImgName);

        String imageUrl = "";
        try {
            imageUrl = s3Upload(storeImageName, multipartFile.getSize(), multipartFile.getContentType(), multipartFile.getInputStream());
        }  catch (IOException e) {
            log.error("이미지 업로드 실패");
        }

        return new CreateRequest(imageUrl, storeImageName);
    }

    //리사이즈한 이미지 업로드
    public CreateRequest saveResizeImage(MultipartFile multipartFile) {
        int width = 300;
        int height = 250;

        String oriImgName = multipartFile.getOriginalFilename();
        String storeImageName = uuidImageName(oriImgName, String.valueOf(width), String.valueOf(height)); //uuidImageName 오버로딩

        String imageUrl = "";
        BufferedImage originalImage;
        try {
            originalImage = ImageIO.read(multipartFile.getInputStream());

            // 원하는 크기로 이미지를 리사이즈
            Image resizedImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);

            BufferedImage bufferedResizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            bufferedResizedImage.getGraphics().drawImage(resizedImage, 0, 0, null);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            ImageIO.write(bufferedResizedImage, "jpg", outputStream);

            byte[] resizedImageBytes = outputStream.toByteArray();

            InputStream inputStream = new ByteArrayInputStream(resizedImageBytes);
            imageUrl = s3Upload(storeImageName, resizedImageBytes.length,  "image/jpeg", inputStream);
        } catch (IOException e) {
            log.error("이미지 리사이즈 실패");
        }

        return new CreateRequest(imageUrl, storeImageName);
    }

    // 저장된 이미지 퍄일 삭제
    @Async
    public void deleteFile(String storeImageName) {
        s3Client.deleteObject(new DeleteObjectRequest(bucket, storeImageName));
    }

    //S3 업로드, 저장된 이미지 URL 반환
    private String s3Upload(String storeImageName, long imageSize, String contentType, InputStream inputStream) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(imageSize);
        objectMetadata.setContentType(contentType);

        s3Client.putObject(new PutObjectRequest(bucket, storeImageName, inputStream, objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        return s3Client.getUrl(bucket, storeImageName).toString();
    }

    //이미지파일 원본이름 랜덤 변경
    private String uuidImageName(String oriImgName) {
        return UUID.randomUUID().toString().concat(getFileExtension(oriImgName));
    }

    //리사이즈 이미지 파일 이름 랜덤 변경
    private String uuidImageName(String oriImageName, String width, String height) {
        return UUID.randomUUID().toString().concat("_" + width + "x" + height + getFileExtension(oriImageName));
    }

    //이미지파일 원본이름 확장자 자르기
    private String getFileExtension(String oriImgName) {
        try {
            return oriImgName.substring(oriImgName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            log.error("확장자 추출 실패");
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
