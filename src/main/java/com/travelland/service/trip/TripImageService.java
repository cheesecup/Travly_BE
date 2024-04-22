package com.travelland.service.trip;

import com.travelland.domain.trip.Trip;
import com.travelland.domain.trip.TripImage;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.repository.trip.TripImageRepository;
import com.travelland.global.s3.S3FileService;
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

    // 이미지 정보 저장, 썸네일 이미지 URL 반환
    @Transactional
    public String createTripImage(MultipartFile thumbnail, List<MultipartFile> imageList, Trip trip) {
        TripImage tripImage = tripImageRepository.save(new TripImage(s3FileService.s3Upload(thumbnail), true, trip)); // 썸네일 이미지 저장

        if (imageList != null) {
            imageList.stream()
                    .map(image -> new TripImage(s3FileService.s3Upload(image), false, trip))
                    .forEach(tripImageRepository::save);
        }

        return tripImage.getImageUrl();
    }

    // 선택한 게시글 이미지 URL 리스트 가져오기
    @Transactional(readOnly = true)
    public List<String> getTripImageUrl(Trip trip) {
        return tripImageRepository.findAllByTrip(trip).stream()
                .map(TripImage::getImageUrl).toList();
    }

    // 선택한 게시글 썸네일 이미지 URL 가져오기
    @Transactional(readOnly = true)
    public String getTripThumbnailUrl(Trip trip) {
        TripImage tripImage =  tripImageRepository.findByTripAndIsThumbnail(trip, true)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_IMAGE_NOT_FOUND));
        return tripImage.getImageUrl();
    }
    
    // 게시글 이미지 삭제
    @Transactional
    public void deleteTripImage(Trip trip) {
        List<String> storeImageNameList = tripImageRepository.findAllByTrip(trip).stream()
                .map(TripImage::getStoreImageName).toList();

        tripImageRepository.deleteByTrip(trip);

        storeImageNameList.forEach(s3FileService::deleteFile); //S3 이미지 삭제
    }
}
