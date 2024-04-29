package com.travelland.repository.trip.es;

import com.travelland.dto.trip.TripDto;
import com.travelland.esdoc.TripSearchDoc;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Component
public class CustomTripSearchRepositoryImpl implements CustomTripSearchRepository {
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public SearchHits<TripSearchDoc> searchByText(String text, Pageable pageable) {
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        searchQueryBuilder.withPageable(pageable);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.minimumShouldMatch(1);
        Arrays.stream(text.split("\\s+"))
                .forEach(word -> {
                    boolQueryBuilder.should(QueryBuilders.matchQuery("title", word));
                    boolQueryBuilder.should(QueryBuilders.matchQuery("content", word));
                    boolQueryBuilder.should(QueryBuilders.matchQuery("area", word));
                    boolQueryBuilder.should(QueryBuilders.matchQuery("hashtag", word));
                });
        boolQueryBuilder.must(QueryBuilders.matchQuery("is_public", true));
        searchQueryBuilder.withQuery(boolQueryBuilder);
        return elasticsearchOperations.search(searchQueryBuilder.build(), TripSearchDoc.class);
    }
    /**
     * 통합 검색기능, hashtag, area, title, content 기준으로 검색
     * @param text 검색할 문자열 입력
     * @param pageable 페이징 처리 입력
     * @return 검색 결과
     */
    @Override
    public SearchHits<TripSearchDoc> searchByTextTEST(String text, Pageable pageable) {
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        searchQueryBuilder.withPageable(pageable);
        searchQueryBuilder.withHighlightFields(
                new HighlightBuilder.Field("hashtag"),
                new HighlightBuilder.Field("area"),
                new HighlightBuilder.Field("eng_kor_hashtag_suggest"),
                new HighlightBuilder.Field("chosung_hashtag"),
                new HighlightBuilder.Field("chosung_area"),
                new HighlightBuilder.Field("ng_kor_area_suggest")
        );

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.minimumShouldMatch(1);
        Arrays.stream(text.split("\\s+"))
                .forEach(word -> {
                    boolQueryBuilder.should(QueryBuilders.matchQuery("title", word));
                    boolQueryBuilder.should(QueryBuilders.matchQuery("content", word));
                    boolQueryBuilder.should(QueryBuilders.matchQuery("area", word));
                    boolQueryBuilder.should(QueryBuilders.matchQuery("hashtag", word));
                    boolQueryBuilder.should(QueryBuilders.matchQuery("eng_kor_hashtag_suggest", word));
                    boolQueryBuilder.should(QueryBuilders.matchQuery("chosung_hashtag", word));
                    boolQueryBuilder.should(QueryBuilders.matchQuery("chosung_area", word));
                    boolQueryBuilder.should(QueryBuilders.matchQuery("eng_kor_area_suggest", word));
                    boolQueryBuilder.should(QueryBuilders.matchQuery("chosung_title", word));
                    boolQueryBuilder.should(QueryBuilders.matchQuery("eng_kor_title_suggest", word));
                });
        boolQueryBuilder.must(QueryBuilders.matchQuery("is_public", true));
        searchQueryBuilder.withQuery(boolQueryBuilder);
        return elasticsearchOperations.search(searchQueryBuilder.build(), TripSearchDoc.class);
    }
    /**
     * 제목 검색기능
     * @param title 검색할 문자열 입력
     * @param pageable 페이징 처리 입력
     * @return 검색 결과
     */
    @Override
    public SearchHits<TripSearchDoc> searchByTitle(String title, Pageable pageable) {
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        searchQueryBuilder.withPageable(pageable);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        Arrays.stream(title.split("\\s+"))
                .forEach(word -> boolQueryBuilder.must(QueryBuilders.matchQuery("title", word)));
        boolQueryBuilder.must(QueryBuilders.matchQuery("is_public", true));
        searchQueryBuilder.withQuery(boolQueryBuilder);

        return elasticsearchOperations.search(searchQueryBuilder.build(), TripSearchDoc.class);
    }
    /**
     * 필드 기준 검색기능
     * @param field 검색할 필드명 입력
     * @param keyword 검색할 필드값 입력
     * @param isPublic 검색 공개글 여부
     * @param pageable 페이징 처리 입력
     * @return 검색 결과
     */
    @Override
    public SearchHits<TripSearchDoc> searchByField(String field, String keyword, boolean isPublic, Pageable pageable) {

        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        searchQueryBuilder.withPageable(pageable);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("is_public", isPublic));
        boolQueryBuilder.must(QueryBuilders.matchQuery(field, keyword));
        searchQueryBuilder.withQuery(boolQueryBuilder);

        return elasticsearchOperations.search(searchQueryBuilder.build(), TripSearchDoc.class);
    }
    /**
     * 지역 검색 기능
     * @param area 검색할 지역명 배열 입력
     * @param isPublic 검색 공개글 여부
     * @param pageable 페이징 처리 입력
     * @return 검색 결과
     */
    @Override
    public SearchHits<TripSearchDoc> searchByArea(String[] area, boolean isPublic, Pageable pageable) {
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        searchQueryBuilder.withPageable(pageable);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.minimumShouldMatch(1);
        Arrays.stream(area).forEach(word -> boolQueryBuilder.should(QueryBuilders.matchQuery("area", word)));
        boolQueryBuilder.must(QueryBuilders.matchQuery("is_public", isPublic));
        searchQueryBuilder.withQuery(boolQueryBuilder);
        return elasticsearchOperations.search(searchQueryBuilder.build(), TripSearchDoc.class);
    }
    /**
     * 전체 여행 지역 대상 결과 반환
     * @param isPublic 검색 공개글 여부
     * @param pageable 페이징 처리 입력
     * @return 검색 결과
     */
    @Override
    public SearchHits<TripSearchDoc> searchAllArea(boolean isPublic, Pageable pageable) {
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        searchQueryBuilder.withPageable(pageable);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("is_public", isPublic));
        searchQueryBuilder.withQuery(boolQueryBuilder);
        return elasticsearchOperations.search(searchQueryBuilder.build(), TripSearchDoc.class);
    }
    /**
     * Top 10 에 해당하는 tripId 리스트를 받아서 순서를 유지한 상태로 Elasticsearch에서 조회하는 기능
     * @param keys 정렬된 tripId 리스트
     * @return Top 10 여행 정보
     */
    @Override
    public List<TripDto.Top10> findRankList(List<Long> keys) {

        List<Query> queries = new ArrayList<>();
        for(Long key : keys){
            NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.must(QueryBuilders.matchQuery("is_public", true));
            boolQueryBuilder.must(QueryBuilders.matchQuery("trip_id", key));
            searchQueryBuilder.withQuery(boolQueryBuilder);
            queries.add(searchQueryBuilder.build());
        }

        List<SearchHits<TripSearchDoc>> result = elasticsearchOperations.multiSearch(queries,TripSearchDoc.class);

        return new ArrayList<>(result.stream()
                .filter(element -> element.getTotalHits() > 0)
                .map(element -> new TripDto.Top10(element.getSearchHit(0).getContent()))
                .toList());
    }
    /**
     * Random 추천 기능
     * @param size 랜덤으로 받을 갯수
     * @return Random 추천 결과
     */
    @Override
    public List<TripDto.GetList> getRandomList(int size) {
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(QueryBuilders.matchAllQuery(),ScoreFunctionBuilders.randomFunction())
                .boostMode(CombineFunction.REPLACE)
                .scoreMode(FunctionScoreQuery.ScoreMode.AVG);

        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        searchQueryBuilder.withPageable(PageRequest.of(0, size));
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(functionScoreQueryBuilder);
        boolQueryBuilder.must(QueryBuilders.matchQuery("is_public", true));
        searchQueryBuilder.withQuery(boolQueryBuilder);

        return elasticsearchOperations.search(searchQueryBuilder.build(), TripSearchDoc.class)
                .stream()
                .map(hit -> new TripDto.GetList(hit.getContent())
                ).toList();
    }
}