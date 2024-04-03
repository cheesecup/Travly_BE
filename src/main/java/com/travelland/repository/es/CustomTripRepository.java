package com.travelland.repository.es;

import com.travelland.document.TripDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.List;

public interface CustomTripRepository {
    Page<TripDocument> searchByTitle(String title, Pageable pageable);

    Page<TripDocument> searchByHashtag(String hashtag, Pageable pageable);
}
