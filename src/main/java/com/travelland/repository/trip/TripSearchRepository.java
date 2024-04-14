package com.travelland.repository.trip;

import com.travelland.esdoc.TripSearchDoc;
import com.travelland.global.job.DataSet;
import com.travelland.repository.trip.es.CustomTripRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TripSearchRepository extends ElasticsearchRepository<TripSearchDoc,Long>, CrudRepository<TripSearchDoc,Long>, CustomTripRepository {
    void deleteByTripId(Long tripId);

    Page<TripSearchDoc> findByEmail(Pageable pageable, String email);

    TripSearchDoc findByTripId(Long tripId);

    Page<TripSearchDoc> findAllByIsPublic(Pageable pageable, Boolean isPublic);


}
