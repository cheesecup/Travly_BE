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

    /**
     * AWS S3 이미지 업로드
     * @param multipartFile 업로드할 이미지 파일
     * @return 이미지 URL, 저장된 이미지 이름
     */
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

    /**
     * 이미지 리사이즈 후 AWS S3 업로드
     * @param multipartFile 리사이즈할 이미지 파일
     * @return 이미지 URL, 저장된 이미지 이름
     */
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

    /**
     * AWS S3 이미지 삭제
     * @param storeImageName 삭제할 이미지 파일 이름
     */
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

    /**
     * 이미지 파일 이름 변경
     * @param oriImgName 변경할 이미지 파일 이름
     * @return 변경된 이미지 파일 이름
     */
    private String uuidImageName(String oriImgName) {
        return UUID.randomUUID().toString().concat(getFileExtension(oriImgName));
    }

    /**
     * 이미지 사이즈가 이름에 포함되도록 이미지 파일 이름 변경
     * @param oriImageName 변경할 이미지 파일 이름
     * @param width 가로
     * @param height 세로
     * @return 변경된 이미지 파일 이름
     */
    private String uuidImageName(String oriImageName, String width, String height) {
        return UUID.randomUUID().toString().concat("_" + width + "x" + height + getFileExtension(oriImageName));
    }

    /**
     * 이미지 파일 이름에서 확장자 추출
     * @param oriImgName 추출할 이미지 파일 이름
     * @return 추출된 확장자
     */
    private String getFileExtension(String oriImgName) {
        try {
            return oriImgName.substring(oriImgName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            log.error("확장자 추출 실패");
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
