package com.travelland.service;

import com.travelland.domain.Trip;
import com.travelland.domain.TripHashtag;
import com.travelland.repository.TripHashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripHashTagService {

    private final TripHashtagRepository tripHashtagRepository;

    // 해쉬태그 생성
    @Transactional
    public void createHashTag(String title, Trip trip) {
        tripHashtagRepository.save(new TripHashtag(title, trip));
    }

    // 해시태그 리스트 가져오기
    @Transactional(readOnly = true)
    public List<String> getHashTagList(Trip trip) {
        return tripHashtagRepository.findAllByTrip(trip).stream()
                .map(TripHashtag::getTitle).toList();
    }
    
    // 해쉬태그 삭제
    @Transactional
    public void deleteHashTag(Trip trip) {
        tripHashtagRepository.deleteByTrip(trip);
    }
}
