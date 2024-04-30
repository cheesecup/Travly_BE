package com.travelland.service.trip;

import com.travelland.domain.trip.Trip;
import com.travelland.domain.trip.TripImage;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.repository.trip.TripImageRepository;
import com.travelland.global.s3.S3FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class TripImageService {

    private final TripImageRepository tripImageRepository;
    private final S3FileService s3FileService;
    private final Executor asyncTaskExecutor;

    /**
     * 회원이 등록한 여행후기 이미지 정보 저장
     * @param thumbnail 썸네일 이미지 파일
     * @param imageList 추가 이미지 파일
     * @param trip 등록할 여행후기
     * @return 썸네일 이미지 URL
     */
    @Transactional
    public String createTripImage(MultipartFile thumbnail, List<MultipartFile> imageList, Trip trip) {
        TripImage tripImage = tripImageRepository.save(new TripImage(s3FileService.saveResizeImage(thumbnail), true, trip)); //리사이즈된 썸네일 이미지 저장
        tripImageRepository.save(new TripImage(s3FileService.saveOriginalImage(thumbnail), false, trip));

        if (imageList != null && !imageList.isEmpty()) {
            imageList.stream()
                    .map(image -> CompletableFuture.supplyAsync(() -> s3FileService.saveOriginalImage(image), asyncTaskExecutor))
                    .toList()
                    .forEach(request -> tripImageRepository.save(new TripImage(request.join(), false, trip)));
        }

        return tripImage.getImageUrl();
    }

    /**
     * 여행후기에 등록된 이미지 URL 목록 조회
     * @param trip 조회할 여행후기
     * @return 조회된 이미지 URL 목록
     */
    @Transactional(readOnly = true)
    public List<String> getTripImageUrl(Trip trip) {
        return tripImageRepository.findAllByTripAndIsThumbnail(trip, false).stream()
                .map(TripImage::getImageUrl).toList();
    }

    /**
     * 여행후기에 등록된 썸네일 URL 조회
     * @param trip 조회할 여행후기
     * @return 조회된 썸네일 URL
     */
    @Transactional(readOnly = true)
    public String getTripThumbnailUrl(Trip trip) {
        TripImage tripImage =  tripImageRepository.findByTripAndIsThumbnail(trip, true)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_IMAGE_NOT_FOUND));
        return tripImage.getImageUrl();
    }

    /**
     * 여행후기 이미지와 S3 이미지 삭제
     * @param trip 삭제할 여행후기
     */
    @Transactional
    public void deleteTripImage(Trip trip) {
        List<String> storeImageNameList = tripImageRepository.findAllByTrip(trip).stream()
                .map(TripImage::getImageUrl).toList();
        storeImageNameList.forEach(s3FileService::deleteFile); //S3 이미지 삭제

        tripImageRepository.deleteByTrip(trip);
    }
}
