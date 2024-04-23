package com.travelland.repository.trip.es;

import com.travelland.dto.trip.TripDto;
import com.travelland.esdoc.TripSearchDoc;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.List;

public interface CustomTripRepository {
    SearchHits<TripSearchDoc> searchByText(String text, Pageable pageable);

    SearchHits<TripSearchDoc> searchByTitle(String title, Pageable pageable);

    SearchHits<TripSearchDoc> searchByField(String field, String keyword, boolean isPublic, Pageable pageable);

    List<TripDto.Top10> findRankList(List<Long> keys);

    List<TripDto.GetList> getRandomList(int size);
}
