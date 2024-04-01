package com.travelland.service;

import com.travelland.domain.Trip;
import com.travelland.domain.TripImage;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.repository.TripImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
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
        imageList.stream()
                .map(image -> new TripImage(s3FileService.s3Upload(image), imageList.indexOf(image) == 0, trip))
                .forEach(tripImageRepository::save);
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

        storeImageNameList.forEach(s3FileService::deleteFile); //S3 이미지 삭제
    }
}
