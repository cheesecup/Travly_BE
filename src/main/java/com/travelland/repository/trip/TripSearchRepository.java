package com.travelland.repository.trip;

import com.travelland.document.TripSearchDoc;
import com.travelland.dto.TripSearchDto;
import com.travelland.repository.es.CustomTripRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TripSearchRepository extends ElasticsearchRepository<TripSearchDoc,Long>, CrudRepository<TripSearchDoc,Long>, CustomTripRepository {

    Page<TripSearchDoc> searchByTitle(String title, Pageable pageable);

    Page<TripSearchDoc> searchByHashtag(String hashtag, Pageable pageable);

    Optional<TripSearchDoc> findByTripId(Long tripId);

    Optional<TripSearchDoc> findByArea(String area);
}
