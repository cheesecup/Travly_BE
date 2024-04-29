package com.travelland.global.elasticsearch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
/**
 * 인기 검색어를 집계하기 위한 로그
 * @author     kjw
 * @version    1.0.0
 * @since      1.0.0
 */
@Slf4j(topic = "ESLog")
@Component
@RequiredArgsConstructor
public class ElasticsearchLogService {
    /**
     * Elasticsearch 통신 관련 method
     */
    private final RestHighLevelClient client;

    /**
     * 로그를 남기기 위한 Elasticsearch index 설정
     * @param indexName 로그를 남길 index 명
     * @param doc index 안 mapping 설정
     */
    public void indexDocument(String indexName, Map<String,Object> doc) {
        IndexRequest request = new IndexRequest(indexName).source(doc);
        try {
            client.indexAsync(request, RequestOptions.DEFAULT, new ActionListener<IndexResponse>() {
                @Override
                public void onResponse(IndexResponse response) {
                    log.debug(response.toString());
                }
                @Override
                public void onFailure(Exception e) {
                    log.error(e.getMessage());
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 로그 기록 하는 기능
     * @param field 로그를 남길 field 값 설정
     * @param query 로그에 해당하는 data 값 입력
     */
    public void putSearchLog(String field, String query) {
        log.info("field : " + field);
        log.info("query : " + query);

        String indexName = "query-log";
        Map<String,Object> doc = new HashMap<>();
        doc.put("field", field);
        doc.put("query", query);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        doc.put("@timestamp", sdf.format(new Date(System.currentTimeMillis())));
        log.info("time : " + sdf.format(new Date(System.currentTimeMillis())));
        indexDocument(indexName, doc);
    }
    /**
     * 로그를 집계하여 상의 10개 데이터를 출력
     * @param field 집계할 field 값 설정
     * @param startTime 집계를 시작할 날짜 입력
     * @param endTime 집계를 종료할 날짜 입력
     * @return 집계결과 상위 10개 map 리스트 반환
     */
    public List<Map<String, Object>> getRankInRange(String field, LocalDateTime startTime, LocalDateTime endTime){
        SearchRequest searchRequest = new SearchRequest("query-log");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(0);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        searchSourceBuilder.query(QueryBuilders.rangeQuery("@timestamp")
                .gte(startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .lte(endTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));

        searchSourceBuilder.query(QueryBuilders.matchQuery("field", field));
        TermsAggregationBuilder aggregation = AggregationBuilders.terms("by_query").field("query.keyword");
        searchSourceBuilder.aggregation(aggregation);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            Terms byQuery = searchResponse.getAggregations().get("by_query");

            return byQuery.getBuckets().stream().map(this::keywordEntryMapper).toList();

        }catch (IOException e){
            log.error(e.getMessage());
            return new ArrayList<>();
        }
    }
    /**
     * 집계결과를 mapping 하기 위한 메서드
     * @param entry 집계 결과
     * @return map - key: id, value: 키워드값, 검색 횟수
     */
    private Map<String, Object> keywordEntryMapper(Terms.Bucket entry){
        Map<String, Object> keywordEntry = new HashMap<>();
        keywordEntry.put("key", entry.getKeyAsString());
        keywordEntry.put("count", entry.getDocCount());
        return keywordEntry;
    }
}