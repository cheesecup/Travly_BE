package com.travelland.repository.trip;

import com.travelland.esdoc.TripSearchDoc;
import com.travelland.repository.trip.es.CustomTripESRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.CrudRepository;

public interface TripSearchESRepository extends ElasticsearchRepository<TripSearchDoc,Long>, CrudRepository<TripSearchDoc,Long>, CustomTripESRepository {
    void deleteByTripId(Long tripId);

    Page<TripSearchDoc> findByEmail(Pageable pageable, String email);

    TripSearchDoc findByTripId(Long tripId);
}
