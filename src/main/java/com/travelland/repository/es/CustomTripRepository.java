package com.travelland.repository.es;

import com.travelland.document.TripSearchDoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomTripRepository {
    Page<TripSearchDoc> searchByTitle(String title, Pageable pageable);

    Page<TripSearchDoc> searchByHashtag(String hashtag, Pageable pageable);

    List<TripSearchDoc> searchByHashtags(String hashtag, Pageable pageable);
}
