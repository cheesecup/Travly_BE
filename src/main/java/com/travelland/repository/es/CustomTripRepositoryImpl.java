package com.travelland.repository.es;

import com.travelland.document.TripSearchDoc;
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
    public Page<TripSearchDoc> searchByTitle(String title, Pageable pageable) {
        Criteria criteria = Criteria.where("title").contains(title);
        Query query = new CriteriaQuery(criteria).setPageable(pageable);

        SearchHits<TripSearchDoc> searchHits = elasticsearchOperations.search(query, TripSearchDoc.class);
        List<TripSearchDoc> tripSearchDocs = searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        return new PageImpl<>(tripSearchDocs, pageable, searchHits.getTotalHits());
    }

    @Override
    public Page<TripSearchDoc> searchByHashtag(String hashtag, Pageable pageable) {
        Criteria criteria = Criteria.where("hashtag").contains(hashtag);
        Query query = new CriteriaQuery(criteria).setPageable(pageable);

        SearchHits<TripSearchDoc> searchHits = elasticsearchOperations.search(query, TripSearchDoc.class);
        List<TripSearchDoc> tripSearchDocs = searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        return new PageImpl<>(tripSearchDocs, pageable, searchHits.getTotalHits());
    }

    @Override
    public List<TripSearchDoc> searchByHashtags(String hashtag, Pageable pageable) {
        Criteria criteria = Criteria.where("hashtag").contains(hashtag);
        Query query = new CriteriaQuery(criteria).setPageable(pageable);

        SearchHits<TripSearchDoc> searchHits = elasticsearchOperations.search(query, TripSearchDoc.class);
        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
}
