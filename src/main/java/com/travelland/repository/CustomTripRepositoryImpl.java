package com.travelland.repository;

import com.travelland.document.TripDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
@RequiredArgsConstructor
@Component
public class CustomTripRepositoryImpl implements CustomTripRepository{
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public Page<TripDocument> searchByTitle(String title, Pageable pageable) {
        Criteria criteria = Criteria.where("title").contains(title);
        Query query = new CriteriaQuery(criteria).setPageable(pageable);

        SearchHits<TripDocument> searchHits = elasticsearchOperations.search(query, TripDocument.class);
        List<TripDocument> tripDocuments = searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        return new PageImpl<>(tripDocuments, pageable, searchHits.getTotalHits());
    }
}
