package com.travelland.repository.es;

import com.travelland.document.TripSearchDoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomTripRepository {
    Page<TripSearchDoc> searchByTitle(String title, Pageable pageable);

    Page<TripSearchDoc> searchByHashtag(String hashtag, Pageable pageable);
}
