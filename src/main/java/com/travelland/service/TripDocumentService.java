package com.travelland.service;

import com.travelland.document.TripDocument;
import com.travelland.dto.TripSearchDto;
import com.travelland.global.exception.CustomException;
import com.travelland.global.exception.ErrorCode;
import com.travelland.repository.TripDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j(topic = "ES")
@Service
@RequiredArgsConstructor
public class TripDocumentService {
    private final TripDocumentRepository tripDocumentRepository;

    public TripSearchDto.GetResponse createTripDocument(TripSearchDto.CreateRequest tripSearchDto){

        return new TripSearchDto.GetResponse(
                tripDocumentRepository.save(new TripDocument(tripSearchDto)));
    }

    public TripSearchDto.GetResponse searchTripById(Long tripId){
        TripDocument tripDocument = tripDocumentRepository.findById(tripId).
                orElseThrow(()-> new CustomException(ErrorCode.POST_NOT_FOUND));
        return new TripSearchDto.GetResponse(tripDocument);
    }

    public void searchTripByTitle(String title){
        Pageable pageable = PageRequest.of(0, 10);
        Page<TripDocument> page = tripDocumentRepository.searchByTitle("여행", pageable);
        log.info(String.valueOf(page.getTotalElements()));
    }

    public void searchTripByArea(String area){
//        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createAt"));
        TripDocument tripDocument = tripDocumentRepository.findByArea("부산").
                orElseThrow(() ->new CustomException( ErrorCode.POST_NOT_FOUND));
        System.out.println("tripDocument = " + tripDocument);

    }
}
