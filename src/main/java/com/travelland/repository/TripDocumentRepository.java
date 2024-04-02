package com.travelland.repository;

import com.travelland.document.TripDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TripDocumentRepository extends ElasticsearchRepository<TripDocument,Long>, CrudRepository<TripDocument,Long>, CustomTripRepository {

    Page<TripDocument> searchByTitle(String title, Pageable pageable);

    Optional<TripDocument> findByArea(String area);
}
