package com.travelland.repository.es;

import com.travelland.document.TripSearchDoc;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
@RequiredArgsConstructor
@Component
public class CustomTripRepositoryImpl implements CustomTripRepository{
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public SearchHits<TripSearchDoc> searchByTitle(String title, Pageable pageable) {
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        searchQueryBuilder.withPageable(pageable);

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        Arrays.stream(title.split("\\s+"))
                .forEach(word -> boolQueryBuilder.must(QueryBuilders.matchQuery("title", word)));

        // 생성된 boolQuery를 검색 쿼리에 설정
        searchQueryBuilder.withQuery(boolQueryBuilder);

        return elasticsearchOperations.search(searchQueryBuilder.build(), TripSearchDoc.class);
    }

    @Override
    public SearchHits<TripSearchDoc> searchByHashtag(String hashtag, Pageable pageable) {

        Query query = new CriteriaQuery(Criteria.where("hashtag").contains(hashtag))
                .setPageable(pageable);

        return elasticsearchOperations.search(query, TripSearchDoc.class);
    }

    @Override
    public List<String> searchByAddress(String address) {

        Query query = new CriteriaQuery(Arrays.stream(address.split("\\s+"))
                .map(part -> Criteria.where("address").contains(part))
                .reduce(Criteria::and)
                .orElseThrow(() -> new IllegalArgumentException("No address parts provided")));

        return elasticsearchOperations.search(query, TripSearchDoc.class)
                .stream()
                .limit(7)
                .map(SearchHit::getContent)
                .map(TripSearchDoc::getAddress)
                .toList();
    }

    @Override
    public SearchHits<TripSearchDoc> searchByEmail(Pageable pageable, String email) {
        Query query = new CriteriaQuery(Criteria.where("email").contains(email))
                .setPageable(pageable);

        return elasticsearchOperations.search(query, TripSearchDoc.class);
    }
}