package com.travelland.repository.trip.es;

import com.travelland.dto.trip.TripDto;
import com.travelland.esdoc.TripSearchDoc;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
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
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

@RequiredArgsConstructor
@Component
public class CustomTripRepositoryImpl implements CustomTripRepository {
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

    @Override
    public SearchHits<TripSearchDoc> searchAllArea(boolean isPublic, Pageable pageable) {
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        searchQueryBuilder.withPageable(pageable);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("is_public", isPublic));
        searchQueryBuilder.withQuery(boolQueryBuilder);
        return elasticsearchOperations.search(searchQueryBuilder.build(), TripSearchDoc.class);
    }

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