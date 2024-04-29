package com.travelland.repository.trip;

import com.travelland.esdoc.TripRecommendDoc;
import com.travelland.repository.trip.es.CustomTripRecommendRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.CrudRepository;

public interface TripRecommendRepository  extends ElasticsearchRepository<TripRecommendDoc,Long>, CrudRepository<TripRecommendDoc,Long>, CustomTripRecommendRepository {
}
