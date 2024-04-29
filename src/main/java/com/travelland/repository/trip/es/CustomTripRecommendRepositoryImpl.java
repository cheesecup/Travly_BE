package com.travelland.repository.trip.es;

import com.travelland.esdoc.TripRecommendDoc;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CustomTripRecommendRepositoryImpl implements CustomTripRecommendRepository {
    private final ElasticsearchOperations elasticsearchOperations;
    /**
     * 추천 여행 정보를 검색, 추천은 OKT 형태소 분석기 사용
     * @param content 추천 기준이 되는 여행글 내용 입력
     * @param size 추천 받을 갯수
     * @return 추천 결과
     */
    @Override
    public SearchHits<TripRecommendDoc> recommendByContent(String content, int size) {
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        searchQueryBuilder.withPageable(PageRequest.of(0,size));
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("content", content));
        boolQueryBuilder.must(QueryBuilders.matchQuery("is_public", true));
        searchQueryBuilder.withQuery(boolQueryBuilder);
        return elasticsearchOperations.search(searchQueryBuilder.build(), TripRecommendDoc.class);
    }
}