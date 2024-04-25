package com.travelland.service.trip;

import com.travelland.dto.trip.TripDto;
import com.travelland.esdoc.TripSearchDoc;
import com.travelland.repository.trip.TripScrapRepository;
import com.travelland.repository.trip.TripSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestTripService {

    private final TripScrapRepository tripScrapRepository;
    private final TripSearchRepository tripSearchRepository;

    //스크랩한 여행정보 목록 조회
    @Transactional(readOnly = true)
    public List<TripDto.Scraps> getTripScrapListTEST(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        List<TripSearchDoc> scrapList = tripScrapRepository.findAllByIsDeleted(false, pageRequest).stream()
                .map(tripScrap -> tripSearchRepository.findByTripId(tripScrap.getTrip().getId())).toList();

        return scrapList.stream().map(TripDto.Scraps::new).toList();
    }
    
    //내가 작성한 여행정보 목록 조회
    public List<TripDto.GetList> getMyTripListTEST(int page, int size) {
        return tripSearchRepository.findAll(PageRequest.of(page-1, size,
                Sort.by(Sort.Direction.DESC, "createdAt")))
                .map(TripDto.GetList::new)
                .getContent();
    }

}
