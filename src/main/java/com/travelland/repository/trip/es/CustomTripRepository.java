package com.travelland.repository.trip.es;

import com.travelland.dto.trip.TripDto;
import com.travelland.esdoc.TripSearchDoc;
import com.travelland.global.job.DataIntSet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.List;

public interface CustomTripRepository {
    SearchHits<TripSearchDoc> searchByTitle(String title, Pageable pageable);

    SearchHits<TripSearchDoc> searchByHashtag(String hashtag, Pageable pageable);

    List<String> searchByAddress(String address);

    SearchHits<TripSearchDoc> searchByEmail(Pageable pageable, String email);

    SearchHits<TripSearchDoc> findAllList(Pageable pageable);

    List<DataIntSet> readViewCount(Pageable pageable);

    List<TripDto.GetList> findRankList(List<Long> keys);

}
