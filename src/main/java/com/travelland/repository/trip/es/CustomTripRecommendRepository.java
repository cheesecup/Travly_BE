package com.travelland.repository.trip.es;

import com.travelland.esdoc.TripRecommendDoc;
import org.springframework.data.elasticsearch.core.SearchHits;

public interface CustomTripRecommendRepository {
    SearchHits<TripRecommendDoc> recommendByContent(String content, int size);
}
