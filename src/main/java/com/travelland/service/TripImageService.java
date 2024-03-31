package com.travelland.service;

import com.travelland.domain.Trip;
import com.travelland.domain.TripImage;
import com.travelland.dto.TripImageDto.CreateRequest;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.repository.TripImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripImageService {

    private final TripImageRepository tripImageRepository;
    private final S3FileService s3FileService;

    // 이미지 정보 저장
    @Transactional
    public void createTripImage(List<MultipartFile> imageList, Trip trip) {
        for (int i=0; i<imageList.size(); i++) {
            boolean isThumbnail = i == 0;

            CreateRequest createRequest = s3FileService.s3Upload(imageList.get(i)); // S3에 이미지 업로드

            tripImageRepository.save(new TripImage(createRequest, isThumbnail, trip));
        }
    }

    // 선택한 게시글 이미지 URL 리스트 가져오기
    @Transactional(readOnly = true)
    public List<String> getTripImageUrl(Trip trip) {
        return tripImageRepository.findAllByTrip(trip).stream()
                .map(TripImage::getImageUrl).toList();
    }

    // 선택한 게시글 썸네일 이미지 URL 가져오기
    @Transactional(readOnly = true)
    public String getTripImageThumbnailUrl(Trip trip) {
        TripImage tripImage =  tripImageRepository.findByTripAndIsThumbnail(trip, true)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_IMAGE_NOT_FOUND));
        return tripImage.getImageUrl();
    }

    @Transactional
    public void deleteTripImage(Trip trip) {
        List<String> storeImageNameList = tripImageRepository.findAllByTrip(trip).stream()
                .map(TripImage::getStoreImageName).toList();

        tripImageRepository.deleteByTrip(trip);

        for (String storeImageName : storeImageNameList) {
            s3FileService.deleteFile(storeImageName); //S3에 저장된 이미지 삭제
        }

    }
}
